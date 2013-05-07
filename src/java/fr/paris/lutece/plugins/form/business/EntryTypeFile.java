/*
 * Copyright (c) 2002-2013, Mairie de Paris
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
package fr.paris.lutece.plugins.form.business;

import fr.paris.lutece.plugins.form.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.business.regularexpression.RegularExpression;
import fr.paris.lutece.portal.service.fileupload.FileUploadService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.regularexpression.RegularExpressionService;
import fr.paris.lutece.portal.web.upload.MultipartHttpServletRequest;
import fr.paris.lutece.portal.web.util.LocalizedPaginator;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.filesystem.FileSystemUtil;
import fr.paris.lutece.util.html.Paginator;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * class EntryTypeFile
 *
 */
public class EntryTypeFile extends AbstractEntryTypeUpload
{
    private final String _template_create = "admin/plugins/form/create_entry_type_file.html";
    private final String _template_modify = "admin/plugins/form/modify_entry_type_file.html";
    private final String _template_html_code = "admin/plugins/form/html_code_entry_type_file.html";

    /**
     * Get the HtmlCode  of   the entry
     * @return the HtmlCode  of   the entry
     *
     * */
    public String getHtmlCode(  )
    {
        return _template_html_code;
    }

    /**
     * Get the request data
     * @param request HttpRequest
     * @param locale the locale
     * @return null if all data requiered are in the request else the url of jsp error
     */
    public String getRequestData( HttpServletRequest request, Locale locale )
    {
        String strTitle = request.getParameter( PARAMETER_TITLE );
        String strHelpMessage = ( request.getParameter( PARAMETER_HELP_MESSAGE ) != null )
            ? request.getParameter( PARAMETER_HELP_MESSAGE ).trim(  ) : null;
        String strComment = request.getParameter( PARAMETER_COMMENT );
        String strMandatory = request.getParameter( PARAMETER_MANDATORY );
        String strCSSClass = request.getParameter( PARAMETER_CSS_CLASS );

        String strError = this.checkEntryData( request, locale );

        if ( StringUtils.isNotBlank( strError ) )
        {
            return strError;
        }

        this.setTitle( strTitle );
        this.setHelpMessage( strHelpMessage );
        this.setComment( strComment );
        this.setCSSClass( strCSSClass );

        this.setFields( request );

        this.setMandatory( strMandatory != null );

        return null;
    }

    /**
     * Get template create url of the entry
     * @return template create url of the entry
     */
    public String getTemplateCreate(  )
    {
        return _template_create;
    }

    /**
     * Get the template modify url  of the entry
     * @return template modify url  of the entry
     */
    public String getTemplateModify(  )
    {
        return _template_modify;
    }

    /**
     * save in the list of response the response associate to the entry in the form submit
     * @param request HttpRequest
     * @param listResponse the list of response associate to the entry in the form submit
     * @param locale the locale
     * @return a Form error object if there is an error in the response
     */
    public FormError getResponseData( HttpServletRequest request, List<Response> listResponse, Locale locale )
    {
        List<FileItem> listFilesSource = null;

        if ( request instanceof MultipartHttpServletRequest )
        {
            List<FileItem> asynchronousFileItems = getFileSources( request );

            if ( asynchronousFileItems != null )
            {
                listFilesSource = asynchronousFileItems;
            }

            FormError formError = null;

            if ( ( listFilesSource != null ) && !listFilesSource.isEmpty(  ) )
            {
                formError = this.checkResponseData( listFilesSource, locale, request );

                if ( formError != null )
                {
                    formError.setUrl( this );

                    // Add the response to the list in order to have the error message in the page
                    Response response = new Response(  );
                    response.setEntry( this );
                    listResponse.add( response );
                }

                for ( FileItem fileItem : listFilesSource )
                {
                    String strFilename = ( fileItem != null ) ? FileUploadService.getFileNameOnly( fileItem )
                                                              : StringUtils.EMPTY;

                    // Add the file to the response list
                    Response response = new Response(  );
                    response.setEntry( this );

                    if ( ( fileItem != null ) && ( fileItem.get(  ) != null ) &&
                            ( fileItem.getSize(  ) < Integer.MAX_VALUE ) )
                    {
                        PhysicalFile physicalFile = new PhysicalFile(  );
                        physicalFile.setValue( fileItem.get(  ) );

                        fr.paris.lutece.plugins.form.business.file.File file = new fr.paris.lutece.plugins.form.business.file.File(  );
                        file.setPhysicalFile( physicalFile );
                        file.setTitle( strFilename );
                        file.setSize( (int) fileItem.getSize(  ) );
                        file.setMimeType( FileSystemUtil.getMIMEType( strFilename ) );

                        response.setFile( file );
                    }

                    listResponse.add( response );

                    String strMimeType = StringUtils.isBlank( strFilename ) ? FileSystemUtil.getMIMEType( strFilename )
                                                                            : StringUtils.EMPTY;
                    List<RegularExpression> listRegularExpression = this.getFields(  ).get( 0 )
                                                                        .getRegularExpressionList(  );

                    if ( StringUtils.isNotBlank( strMimeType ) && ( listRegularExpression != null ) &&
                            ( listRegularExpression.size(  ) != 0 ) &&
                            RegularExpressionService.getInstance(  ).isAvailable(  ) )
                    {
                        for ( RegularExpression regularExpression : listRegularExpression )
                        {
                            if ( !RegularExpressionService.getInstance(  ).isMatches( strMimeType, regularExpression ) )
                            {
                                formError = new FormError(  );
                                formError.setMandatoryError( false );
                                formError.setTitleQuestion( this.getTitle(  ) );
                                formError.setErrorMessage( regularExpression.getErrorMessage(  ) );
                                formError.setUrl( this );
                            }
                        }
                    }
                }
            }
            else if ( this.isMandatory(  ) )
            {
                formError = new MandatoryFormError( this, locale );

                Response response = new Response(  );
                response.setEntry( this );
                listResponse.add( response );
            }

            return formError;
        }

        return new MandatoryFormError( this, locale );
    }

    /**
     * The paginator who is use in the template modify of the entry
     * @param nItemPerPage Number of items to display per page
    * @param strBaseUrl The base Url for build links on each page link
    * @param strPageIndexParameterName The parameter name for the page index
    * @param strPageIndex The current page index
     * @return the paginator who is use in the template modify of the entry
     */
    public Paginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex )
    {
        return new Paginator( this.getFields(  ).get( 0 ).getRegularExpressionList(  ), nItemPerPage, strBaseUrl,
            strPageIndexParameterName, strPageIndex );
    }

    /**
     * return the list of regular expression whose not associate to the entry
     * @param entry the entry
     * @param plugin the plugin
     * @return the list of regular expression whose not associate to the entry
     */
    public ReferenceList getReferenceListRegularExpression( IEntry entry, Plugin plugin )
    {
        ReferenceList refListRegularExpression = null;

        if ( RegularExpressionService.getInstance(  ).isAvailable(  ) )
        {
            refListRegularExpression = new ReferenceList(  );

            List<RegularExpression> listRegularExpression = RegularExpressionService.getInstance(  )
                                                                                    .getAllRegularExpression(  );

            for ( RegularExpression regularExpression : listRegularExpression )
            {
                if ( !entry.getFields(  ).get( 0 ).getRegularExpressionList(  ).contains( regularExpression ) )
                {
                    refListRegularExpression.addItem( regularExpression.getIdExpression(  ),
                        regularExpression.getTitle(  ) );
                }
            }
        }

        return refListRegularExpression;
    }

    /**
     * Get the response value  associate to the entry  to write in the recap
     * @param response the response associate to the entry
     * @param locale the locale
     * @param request the request
     * @return the response value  associate to the entry  to write in the recap
     */
    public String getResponseValueForRecap( HttpServletRequest request, Response response, Locale locale )
    {
        if ( ( response.getFile(  ) != null ) && StringUtils.isNotBlank( response.getFile(  ).getTitle(  ) ) )
        {
            return response.getFile(  ).getTitle(  );
        }
        else
        {
            return StringUtils.EMPTY;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LocalizedPaginator getPaginator( int nItemPerPage, String strBaseUrl, String strPageIndexParameterName,
        String strPageIndex, Locale locale )
    {
        return new LocalizedPaginator( this.getFields(  ).get( 0 ).getRegularExpressionList(  ), nItemPerPage,
            strBaseUrl, strPageIndexParameterName, strPageIndex, locale );
    }

    /**
     * toStringValue should stay <code>null</code>.
     */
    @Override
    public void setResponseToStringValue( Response response, Locale locale )
    {
        // nothing - null is default
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFile(  )
    {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setFields( HttpServletRequest request, List<Field> listFields )
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FormError checkResponseData( FileItem fileItem, Locale locale )
    {
        return null;
    }
}
