/*
 * Copyright (c) 2002-2008, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.form.service.validator;

import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.portal.service.message.SiteMessageException;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;


/**
 * ValidatorService
 */
public class ValidatorService
{
    private static ValidatorService _singleton;

    /**
     * Creates a new instance of ValidatorService
     */
    private ValidatorService(  )
    {
    }

    /**
     * Returns the unique instance of ValidatorService
     * @return the unique instance of ValidatorService
     */
    public static ValidatorService getInstance(  )
    {
        if ( _singleton == null )
        {
            _singleton = new ValidatorService(  );
        }

        return _singleton;
    }

    /**
     * Returns all validators
     * @return all validators
     */
    public Collection<IValidator> getAllValidators(  )
    {
        return SpringContextService.getBeansOfType( IValidator.class );
    }

    /**
     * Validates the form using all validators
     * @param request {@link HttpServletRequest}
     * @param formSubmit the form submit
     * @param plugin the plugin
     * @throws SiteMessageException
     */
    public void validateForm( HttpServletRequest request, FormSubmit formSubmit, Plugin plugin )
        throws SiteMessageException
    {
        for ( IValidator validator : getAllValidators(  ) )
        {
            validator.validateForm( request, formSubmit, plugin );
        }
    }

    /**
     * Checks if at least one validator is associated with the form
     * @param nIdForm the form id
     * @return true if at least one validator is associated with the form, otherwise false
     */
    public boolean isAssociatedWithForm( int nIdForm )
    {
        for ( IValidator validator : getAllValidators(  ) )
        {
            if ( validator.isAssociatedWithForm( nIdForm ) )
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Removes the associations between all validators and the form
     * @param nIdForm the form id
     */
    public void removeAssociationsWithForm( int nIdForm )
    {
        for ( IValidator validator : getAllValidators(  ) )
        {
            validator.removeAssociationsWithForm( nIdForm );
        }
    }
}
