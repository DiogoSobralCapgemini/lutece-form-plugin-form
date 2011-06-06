/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.form.business.outputprocessor;

import fr.paris.lutece.portal.service.plugin.Plugin;

import java.util.List;


public interface IFormProcessorDAO
{
    /**
     * Load the data of all the  FormProcessor wich is associate to the form and returns them as a list
     * @param nIdForm the id of the form
     * @param plugin the plugin
     * @return a List of FormProcessor Object
     */
    List<FormProcessor> selectByIdForm( int nIdForm, Plugin plugin );

    /**
     * Load the data of all the FormProcessor and returns them as a List
     * @param plugin the plugin
     * @return a List of FormProcessor Object
     */
    List<FormProcessor> selectAll( Plugin plugin );

    /**
     * Insert a new record in the table.
     * @param formProcessor formProcessor
     * @param plugin the plugin
     */
    void insert( FormProcessor formProcessor, Plugin plugin );

    /**
     * Delete a record from the table
     * @param formProcessor formProcesor
     * @param plugin the plugin
     */
    void delete( FormProcessor formProcessor, Plugin plugin );
}
