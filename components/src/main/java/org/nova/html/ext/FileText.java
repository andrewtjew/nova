package org.nova.html.ext;

import org.nova.html.elements.Composer;
import org.nova.utils.FileUtils;

public class FileText extends Text
{
    public FileText(String textFileName) throws Throwable
    {
        super(FileUtils.readTextFile(textFileName));
    }

}
