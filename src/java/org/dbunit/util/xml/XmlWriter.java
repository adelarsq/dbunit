package org.dbunit.util.xml;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Commons" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Turbine", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

import java.io.IOException;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;

import java.util.Stack;

/**
 * Makes writing XML much much easier.
 * Improved from
 * <a href="http://builder.com.com/article.jhtml?id=u00220020318yan01.htm&page=1&vf=tt">article</a>
 *
 * @author <a href="mailto:bayard@apache.org">Henri Yandell</a>
 * @author <a href="mailto:pete@fingertipsoft.com">Peter Cassetta</a>
 * @version 1.0
 */
public class XmlWriter
{

    private Writer out;      // underlying writer
    private String encoding;
    private Stack stack = new Stack();        // of xml element names
    private StringBuffer attrs; // current attribute string
    private boolean empty;      // is the current node empty
    private boolean closed = true;     // is the current node closed...

    private boolean pretty = true;    // is pretty printing enabled?
    private boolean wroteText = false; // was text the last thing output?
    private String indent = "  ";     // output this to indent one level when pretty printing
    private String newline = "\n";    // output this to end a line when pretty printing

    /**
     * Create an XmlWriter on top of an existing java.io.Writer.
     */
    public XmlWriter(Writer writer)
    {
        this(writer, null);
    }

    /**
     * Create an XmlWriter on top of an existing java.io.Writer.
     */
    public XmlWriter(Writer writer, String encoding)
    {
        setWriter(writer, encoding);
    }

    /**
     * Turn pretty printing on or off.
     * Pretty printing is enabled by default, but it can be turned off
     * to generate more compact XML.
     *
     * @param boolean true to enable, false to disable pretty printing.
     */
    public void enablePrettyPrint(boolean enable)
    {
        this.pretty = enable;
    }

    /**
     * Specify the string to prepend to a line for each level of indent.
     * It is 2 spaces ("  ") by default. Some may prefer a single tab ("\t")
     * or a different number of spaces. Specifying an empty string will turn
     * off indentation when pretty printing.
     *
     * @param String representing one level of indentation while pretty printing.
     */
    public void setIndent(String indent)
    {
        this.indent = indent;
    }

    /**
     * Specify the string used to terminate each line when pretty printing.
     * It is a single newline ("\n") by default. Users who need to read
     * generated XML documents in Windows editors like Notepad may wish to
     * set this to a carriage return/newline sequence ("\r\n"). Specifying
     * an empty string will turn off generation of line breaks when pretty
     * printing.
     *
     * @param String representing the newline sequence when pretty printing.
     */
    public void setNewline(String newline)
    {
        this.newline = newline;
    }

    /**
     * A helper method. It writes out an element which contains only text.
     *
     * @param name String name of tag
     * @param text String of text to go inside the tag
     */
    public XmlWriter writeElementWithText(String name, String text) throws IOException
    {
        writeElement(name);
        writeText(text);
        return endElement();
    }

    /**
     * A helper method. It writes out empty entities.
     *
     * @param name String name of tag
     */
    public XmlWriter writeEmptyElement(String name) throws IOException
    {
        writeElement(name);
        return endElement();
    }

    /**
     * Begin to write out an element. Unlike the helper tags, this tag
     * will need to be ended with the endElement method.
     *
     * @param name String name of tag
     */
    public XmlWriter writeElement(String name) throws IOException
    {
        return openElement(name);
    }

    /**
     * Begin to output an element.
     *
     * @param String name of element.
     */
    private XmlWriter openElement(String name) throws IOException
    {
        boolean wasClosed = this.closed;
        closeOpeningTag();
        this.closed = false;
        if (this.pretty)
        {
            //   ! wasClosed separates adjacent opening tags by a newline.
            // this.wroteText makes sure an element embedded within the text of
            // its parent element begins on a new line, indented to the proper
            // level. This solves only part of the problem of pretty printing
            // entities which contain both text and child entities.
            if (!wasClosed || this.wroteText)
            {
                this.out.write(newline);
            }
            for (int i = 0; i < this.stack.size(); i++)
            {
                this.out.write(indent); // Indent opening tag to proper level
            }
        }
        this.out.write("<");
        this.out.write(name);
        stack.add(name);
        this.empty = true;
        this.wroteText = false;
        return this;
    }

    // close off the opening tag
    private void closeOpeningTag() throws IOException
    {
        if (!this.closed)
        {
            writeAttributes();
            this.closed = true;
            this.out.write(">");
        }
    }

    // write out all current attributes
    private void writeAttributes() throws IOException
    {
        if (this.attrs != null)
        {
            this.out.write(this.attrs.toString());
            this.attrs.setLength(0);
            this.empty = false;
        }
    }

    /**
     * Write an attribute out for the current element.
     * Any xml characters in the value are escaped.
     * Currently it does not actually throw the exception, but
     * the api is set that way for future changes.
     *
     * @param String name of attribute.
     * @param String value of attribute.
     */
    public XmlWriter writeAttribute(String attr, String value) throws IOException
    {

        // maintain api
        if (false) throw new IOException();

        if (this.attrs == null)
        {
            this.attrs = new StringBuffer();
        }
        this.attrs.append(" ");
        this.attrs.append(attr);
        this.attrs.append("=\"");
        this.attrs.append(escapeXml(value));
        this.attrs.append("\"");
        return this;
    }

    /**
     * End the current element. This will throw an exception
     * if it is called when there is not a currently open
     * element.
     */
    public XmlWriter endElement() throws IOException
    {
        if (this.stack.empty())
        {
            throw new IOException("Called endElement too many times. ");
        }
        String name = (String)this.stack.pop();
        if (name != null)
        {
            if (this.empty)
            {
                writeAttributes();
                this.out.write("/>");
            }
            else
            {
                if (this.pretty && !this.wroteText)
                {
                    for (int i = 0; i < this.stack.size(); i++)
                    {
                        this.out.write(indent); // Indent closing tag to proper level
                    }
                }
                this.out.write("</");
                this.out.write(name);
                this.out.write(">");
            }
            if (this.pretty)
                this.out.write(newline); // Add a newline after the closing tag
            this.empty = false;
            this.closed = true;
            this.wroteText = false;
        }
        return this;
    }

    /**
     * Close this writer. It does not close the underlying
     * writer, but does throw an exception if there are
     * as yet unclosed tags.
     */
    public void close() throws IOException
    {
        this.out.flush();

        if (!this.stack.empty())
        {
            throw new IOException("Tags are not all closed. " +
                    "Possibly, " + this.stack.pop() + " is unclosed. ");
        }
    }

    /**
     * Output body text. Any xml characters are escaped.
     */
    public XmlWriter writeText(String text) throws IOException
    {
        closeOpeningTag();
        this.empty = false;
        this.wroteText = true;
        this.out.write(escapeXml(text));
        return this;
    }

    /**
     * Write out a chunk of CDATA. This helper method surrounds the
     * passed in data with the CDATA tag.
     *
     * @param String of CDATA text.
     */
    public XmlWriter writeCData(String cdata) throws IOException
    {
        closeOpeningTag();
        this.empty = false;
        this.wroteText = true;
        this.out.write("<![CDATA[");
        this.out.write(cdata);
        this.out.write("]]>");
        return this;
    }

    /**
     * Write out a chunk of comment. This helper method surrounds the
     * passed in data with the xml comment tag.
     *
     * @param String of text to comment.
     */
    public XmlWriter writeComment(String comment) throws IOException
    {
        writeChunk("<!-- " + comment + " -->");
        return this;
    }

    private void writeChunk(String data) throws IOException
    {
        closeOpeningTag();
        this.empty = false;
        if (this.pretty && !this.wroteText)
        {
            for (int i = 0; i < this.stack.size(); i++)
            {
                this.out.write(indent);
            }
        }

        this.out.write(data);

        if (this.pretty)
        {
            this.out.write(newline);
        }
    }

    // Two example methods. They should output the same XML:
    // <person name="fred" age="12"><phone>425343</phone><bob/></person>
    static public void main(String[] args) throws IOException
    {
        test1();
        test2();
    }

    static public void test1() throws IOException
    {
        Writer writer = new java.io.StringWriter();
        XmlWriter xmlwriter = new XmlWriter(writer);
        xmlwriter.writeElement("person").writeAttribute("name", "fred").writeAttribute("age", "12").writeElement("phone").writeText("4254343").endElement().writeElement("friends").writeElement("bob").endElement().writeElement("jim").endElement().endElement().endElement();
        xmlwriter.close();
        System.err.println(writer.toString());
    }

    static public void test2() throws IOException
    {
        Writer writer = new java.io.StringWriter();
        XmlWriter xmlwriter = new XmlWriter(writer);
        xmlwriter.writeComment("Example of XmlWriter running");
        xmlwriter.writeElement("person");
        xmlwriter.writeAttribute("name", "fred");
        xmlwriter.writeAttribute("age", "12");
        xmlwriter.writeElement("phone");
        xmlwriter.writeText("4254343");
        xmlwriter.endElement();
        xmlwriter.writeComment("Examples of empty tags");
//        xmlwriter.setDefaultNamespace("test");
        xmlwriter.writeElement("friends");
        xmlwriter.writeEmptyElement("bob");
        xmlwriter.writeEmptyElement("jim");
        xmlwriter.endElement();
        xmlwriter.writeElementWithText("foo", "This is an example.");
        xmlwriter.endElement();
        xmlwriter.close();
        System.err.println(writer.toString());
    }

    ////////////////////////////////////////////////////////////////////////////
    // Added by Manu for DbUnit

    private String escapeXml(String str)
    {
        str = replace(str, "&", "&amp;");
        str = replace(str, "<", "&lt;");
        str = replace(str, ">", "&gt;");
        str = replace(str, "\"", "&quot;");
        str = replace(str, "'", "&apos;");
        return str;
    }

    private String replace(String value, String original, String replacement)
    {
        StringBuffer buffer = null;

        int startIndex = 0;
        int lastEndIndex = 0;
        for (; ;)
        {
            startIndex = value.indexOf(original, lastEndIndex);
            if (startIndex == -1)
            {
                if (buffer != null)
                {
                    buffer.append(value.substring(lastEndIndex));
                }
                break;
            }

            if (buffer == null)
            {
                buffer = new StringBuffer((int)(original.length() * 1.5));
            }
            buffer.append(value.substring(lastEndIndex, startIndex));
            buffer.append(replacement);
            lastEndIndex = startIndex + original.length();
        }

        return buffer == null ? value : buffer.toString();
    }

    private void setEncoding(String encoding)
    {
        if (encoding == null && out instanceof OutputStreamWriter)
            encoding = ((OutputStreamWriter)out).getEncoding();

        if (encoding != null)
        {
            encoding = encoding.toUpperCase();

            // Use official encoding names where we know them,
            // avoiding the Java-only names.  When using common
            // encodings where we can easily tell if characters
            // are out of range, we'll escape out-of-range
            // characters using character refs for safety.

            // I _think_ these are all the main synonyms for these!
            if ("UTF8".equals(encoding))
            {
                encoding = "UTF-8";
            }
            else if ("US-ASCII".equals(encoding)
                    || "ASCII".equals(encoding))
            {
//                dangerMask = (short)0xff80;
                encoding = "US-ASCII";
            }
            else if ("ISO-8859-1".equals(encoding)
                    || "8859_1".equals(encoding)
                    || "ISO8859_1".equals(encoding))
            {
//                dangerMask = (short)0xff00;
                encoding = "ISO-8859-1";
            }
            else if ("UNICODE".equals(encoding)
                    || "UNICODE-BIG".equals(encoding)
                    || "UNICODE-LITTLE".equals(encoding))
            {
                encoding = "UTF-16";

                // TODO: UTF-16BE, UTF-16LE ... no BOM; what
                // release of JDK supports those Unicode names?
            }

//            if (dangerMask != 0)
//                stringBuf = new StringBuffer();
        }

        this.encoding = encoding;
    }


    /**
     * Resets the handler to write a new text document.
     *
     * @param writer XML text is written to this writer.
     * @param encoding if non-null, and an XML declaration is written,
     *	this is the name that will be used for the character encoding.
     *
     * @exception IllegalStateException if the current
     *	document hasn't yet ended (with {@link #endDocument})
     */
    final public void setWriter(Writer writer, String encoding)
    {
        if (this.out != null)
            throw new IllegalStateException(
                    "can't change stream in mid course");
        this.out = writer;
        if (this.out != null)
            setEncoding(encoding);
//        if (!(this.out instanceof BufferedWriter))
//            this.out = new BufferedWriter(this.out);
    }

    public XmlWriter writeDeclaration() throws IOException
    {
        if (this.encoding != null)
        {
            this.out.write("<?xml version='1.0'");
            this.out.write(" encoding='" + this.encoding + "'");
            this.out.write("?>");
            this.out.write(this.newline);
        }

        return this;
    }

}
