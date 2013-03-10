/****************************************************************************/
/*  File:       DevNullOutputStream.java                                    */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:       2009-07-28                                                  */
/*  Tags:                                                                   */
/*      Copyright (c) 2009, 2010 Florent Georges (see end of file.)         */
/* ------------------------------------------------------------------------ */
package transform;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author aretter
 */
public class DevNullOutputStream
            extends OutputStream
{
    @Override
    public void close() throws IOException {
        // nothing
    }
    @Override
    public void flush() throws IOException {
        // nothing
    }
    @Override
    public void write(final byte[] b) throws IOException {
        // nothing
    }
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        // nothing
    }
    @Override
    public void write(final int b) throws IOException {
        // nothing
    }
}


/* ------------------------------------------------------------------------ */
/*  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS COMMENT.               */
/*                                                                          */
/*  The contents of this file are subject to the Mozilla Public License     */
/*  Version 1.0 (the "License"); you may not use this file except in        */
/*  compliance with the License. You may obtain a copy of the License at    */
/*  http://www.mozilla.org/MPL/.                                            */
/*                                                                          */
/*  Software distributed under the License is distributed on an "AS IS"     */
/*  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.  See    */
/*  the License for the specific language governing rights and limitations  */
/*  under the License.                                                      */
/*                                                                          */
/*  The Original Code is: all this file.                                    */
/*                                                                          */
/*  The Initial Developer of the Original Code is Florent Georges.          */
/*                                                                          */
/*  Contributor(s): Adam Retter                                             */
/* ------------------------------------------------------------------------ */