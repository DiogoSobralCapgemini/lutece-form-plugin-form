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
package fr.paris.lutece.plugins.form.service.export;

import fr.paris.lutece.plugins.form.business.ExportFormat;
import fr.paris.lutece.plugins.form.business.Form;
import fr.paris.lutece.plugins.form.business.FormSubmit;
import fr.paris.lutece.plugins.form.utils.FileUtils;
import fr.paris.lutece.plugins.form.utils.FormUtils;
import fr.paris.lutece.portal.service.html.XmlTransformerService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.xml.XmlUtil;

import java.io.IOException;
import java.util.List;


/**
 *
 * FormSubmitExportService
 *
 */
public class FormSubmitExportService extends AbstractExportService
{
    /**
     * The name of the bean of this service
     */
    public static final String BEAN_SERVICE = "form.formSubmitExportService";

    /**
     * {@inheritDoc}
     */
    @Override
    public void doExport( Form form, List<FormSubmit> listFormSubmit, String strFolderPath, ExportFormat exportFormat,
        String strEncoding, StringBuilder sbLog, Plugin plugin )
    {
        if ( ( listFormSubmit != null ) && !listFormSubmit.isEmpty(  ) )
        {
            for ( FormSubmit formSubmit : listFormSubmit )
            {
                // Build the file name
                String strFileName = FileUtils.buildFileName( form.getIdForm(  ) + FormUtils.CONSTANT_UNDERSCORE +
                        formSubmit.getIdFormSubmit(  ), exportFormat.getExtension(  ).trim(  ) );

                // Build the XML of the form submit
                String strXmlSource = XmlUtil.getXmlHeader(  ) +
                    FormUtils.getXmlResponses( null, form, formSubmit, null, plugin );

                // XSL transform to the desired output file
                String strXslUniqueId = XSL_UNIQUE_PREFIX_ID + exportFormat.getIdExport(  );
                XmlTransformerService xmlTransformerService = new XmlTransformerService(  );
                String strFileOutPut = xmlTransformerService.transformBySourceWithXslCache( strXmlSource,
                        exportFormat.getXsl(  ), strXslUniqueId, null, null );

                try
                {
                    FileUtils.createFile( strFolderPath, strFileName, strFileOutPut, strEncoding );
                    sbLog.append( "\n\tForm submit ID " + formSubmit.getIdFormSubmit(  ) + " exported." );
                }
                catch ( IOException e )
                {
                    AppLogService.error( e.getMessage(  ), e );
                    sbLog.append( "\n\tERROR when writing file " + strFileName );
                }
            }
        }
    }
}
