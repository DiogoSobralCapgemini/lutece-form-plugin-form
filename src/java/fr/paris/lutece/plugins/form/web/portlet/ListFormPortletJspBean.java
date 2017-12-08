/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.form.web.portlet;

import fr.paris.lutece.plugins.form.business.Category;
import fr.paris.lutece.plugins.form.business.CategoryHome;
import fr.paris.lutece.plugins.form.business.portlet.ListFormPortlet;
import fr.paris.lutece.plugins.form.business.portlet.ListFormPortletHome;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.plugins.form.web.FormJspBean;
import fr.paris.lutece.portal.business.portlet.PortletHome;
import fr.paris.lutece.portal.business.portlet.PortletType;
import fr.paris.lutece.portal.business.portlet.PortletTypeHome;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.web.portlet.PortletJspBean;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/**
 * This class provides the user interface to manage form Portlet
 */
public class ListFormPortletJspBean extends PortletJspBean
{
    // //////////////////////////////////////////////////////////////////////////
    // Constants
    /**
     * Right to manage forms
     */
    public static final String RIGHT_MANAGE_FORM = FormJspBean.RIGHT_MANAGE_FORM;
    private static final long serialVersionUID = -2619049973871862338L;
    private static final String MARK_CATEGORIES_LIST = "categories_list";
    private static final String MARK_ID_DEFAULT_CATEGORY = "id_category";
    private static final String PARAMETER_ID_CATEGORY = "id_category";
    private static final String MESSAGE_YOU_MUST_CHOOSE_A_CATEGORY = "form.message.mandatory.category";

    // //////////////////////////////////////////////////////////////////////////
    // Class attributes

    /**
     * Returns the Download portlet creation form
     *
     * @param request
     *            The HTTP request
     * @return The HTML form
     */
    @Override
    public String getCreate( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>( );
        String strIdPage = request.getParameter( PARAMETER_PAGE_ID );
        String strIdPortletType = request.getParameter( PARAMETER_PORTLET_TYPE_ID );
        PortletType portletType = PortletTypeHome.findByPrimaryKey( strIdPortletType );
        Plugin plugin = PluginService.getPlugin( portletType.getPluginName( ) );
        ReferenceList refList = FormUtils.getRefListCategory( CategoryHome.getList( plugin ) );

        model.put( MARK_CATEGORIES_LIST, refList );

        HtmlTemplate template = getCreateTemplate( strIdPage, strIdPortletType, model );

        return template.getHtml( );
    }

    /**
     * Returns the Download portlet modification form
     *
     * @param request
     *            The Http request
     * @return The HTML form
     */
    @Override
    public String getModify( HttpServletRequest request )
    {
        HashMap<String, Object> model = new HashMap<String, Object>( );
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        int nPortletId = -1;

        try
        {
            nPortletId = Integer.parseInt( strPortletId );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        ListFormPortlet portlet = (ListFormPortlet) PortletHome.findByPrimaryKey( nPortletId );
        Plugin plugin = PluginService.getPlugin( portlet.getPluginName( ) );
        Category category = CategoryHome.findByPrimaryKey( portlet.getIdCategory( ), plugin );

        ReferenceList refList = FormUtils.getRefListCategory( CategoryHome.getList( plugin ) );
        model.put( MARK_CATEGORIES_LIST, refList );
        model.put( MARK_ID_DEFAULT_CATEGORY, category.getIdCategory( ) );

        HtmlTemplate template = getModifyTemplate( portlet, model );

        return template.getHtml( );
    }

    /**
     * Process portlet's creation
     *
     * @param request
     *            The Http request
     * @return The Jsp management URL of the process result
     */
    @Override
    public String doCreate( HttpServletRequest request )
    {
        ListFormPortlet portlet = new ListFormPortlet( );
        String strPageId = request.getParameter( PARAMETER_PAGE_ID );
        String strFormId = request.getParameter( PARAMETER_ID_CATEGORY );
        int nPageId = -1;
        int nCategoryId = -1;

        // get portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        try
        {
            nPageId = Integer.parseInt( strPageId );
            nCategoryId = Integer.parseInt( strFormId );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        if ( ( strErrorUrl == null ) && ( nCategoryId == -1 ) )
        {
            strErrorUrl = AdminMessageService.getMessageUrl( request, MESSAGE_YOU_MUST_CHOOSE_A_CATEGORY, AdminMessage.TYPE_STOP );
        }

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.setPageId( nPageId );
        portlet.setIdCategory( nCategoryId );

        // Creating portlet
        ListFormPortletHome.getInstance( ).create( portlet );

        // Displays the page with the new Portlet
        return getPageUrl( nPageId );
    }

    /**
     * Process portlet's modification
     *
     * @param request
     *            The http request
     * @return Management's Url
     */
    @Override
    public String doModify( HttpServletRequest request )
    {
        // recovers portlet attributes
        String strPortletId = request.getParameter( PARAMETER_PORTLET_ID );
        String strCategoryId = request.getParameter( PARAMETER_ID_CATEGORY );
        int nPortletId = -1;
        int nCategoryId = -1;

        try
        {
            nPortletId = Integer.parseInt( strPortletId );
            nCategoryId = Integer.parseInt( strCategoryId );
        }
        catch( NumberFormatException ne )
        {
            AppLogService.error( ne );
        }

        ListFormPortlet portlet = (ListFormPortlet) PortletHome.findByPrimaryKey( nPortletId );

        // retrieve portlet common attributes
        String strErrorUrl = setPortletCommonData( request, portlet );

        if ( ( strErrorUrl == null ) && ( nCategoryId == -1 ) )
        {
            strErrorUrl = AdminMessageService.getMessageUrl( request, MESSAGE_YOU_MUST_CHOOSE_A_CATEGORY, AdminMessage.TYPE_STOP );
        }

        if ( strErrorUrl != null )
        {
            return strErrorUrl;
        }

        portlet.setIdCategory( nCategoryId );
        // updates the portlet
        portlet.update( );

        // displays the page withe the potlet updated
        return getPageUrl( portlet.getPageId( ) );
    }
}