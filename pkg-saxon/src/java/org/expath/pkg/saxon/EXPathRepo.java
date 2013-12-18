/****************************************************************************/
/*  File:       EXPathRepo.java                                             */
/*  Author:     F. Georges                                                  */
/*  Company:    H2O Consulting                                              */
/*  Date:                                                                   */
/*  Tags:                                                                   */
/*      Copyright (c) 2013 Florent Georges (see end of file.)               */
/* ------------------------------------------------------------------------ */


package org.expath.pkg.saxon;

import java.io.File;
import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

/**
 * TODO: ...
 * 
 * TODO: Is it still used?
 *
 * @author Florent Georges
 */
public class EXPathRepo
{
    public EXPathRepo(File repo)
    {
        myRepo = repo;
    }

    public Configuration newConfiguration()
    {
        return new EXPathConfiguration(this);
    }

    public TransformerFactoryImpl newTransformerFactory()
    {
        Configuration conf = new EXPathConfiguration(this);
        return new TransformerFactoryImpl(conf);
    }

    // TODO: No.  Would require a proxy configuration, reusing the original
    // config object, except for a few methods...
    // TODO: Same principle for Processor.
    //
//    public void configTransformerFactory(TransformerFactoryImpl factory)
//    {
//        Configuration conf = new EXPathConfiguration(this);
//        factory.setConfiguration(conf);
//    }

    // TODO: There is no way to set the config object on a processor...
    //
//    public Processor newProcessor()
//    {
//        // TODO: Support an SA processor...
//        Processor proc = new Processor(false);
//        Configuration conf = new EXPathConfiguration(this);
//        ...
//    }

    private File myRepo;
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
/*  Contributor(s): none.                                                   */
/* ------------------------------------------------------------------------ */
