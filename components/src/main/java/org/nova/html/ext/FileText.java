package org.nova.html.ext;

import org.nova.utils.FileUtils;

public class FileText extends LiteralHtml
{
    public FileText(String textFileName) throws Throwable
    {
        super(FileUtils.readString(textFileName));
    }

}
