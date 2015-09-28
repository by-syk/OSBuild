/**
 * @author By_syk
 */

package com.by_syk.osbuild.util;

import java.util.jar.JarFile;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AndroidManifestUtil
{
    private static String AM_XML = "AndroidManifest.xml";
    
    public static String readAM(String xml_path)
    {
        String result = "";
        if (xml_path == null || !(new File(xml_path)).getName().equals(AM_XML))
        {
            return result;
        }
        
        try
        {
            InputStream inputStream = new FileInputStream(xml_path);
            result = readAM(inputStream);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public static String readAMFromAPK(String apk_path)
    {
        String result = "";
        if (apk_path == null || !apk_path.endsWith(".apk"))
        {
            return result;
        }
        
        try
        {
            JarFile jarFile = new JarFile(apk_path);
            result = readAM(jarFile.getInputStream(jarFile.getEntry(AM_XML)));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return result;
    }
    
    public static String readAM(InputStream inputStream)
    {
        String result = "";
        try
        {
            byte[] buffer = new byte[inputStream.available()];
            if (inputStream.read(buffer) > 0)
            {
                result = decompressXML(buffer);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {}
            }
        }

        return result;
    }
    
    private static int END_DOC_TAG = 0x00100101;
    private static int START_TAG = 0x00100102;
    private static int END_TAG = 0x00100103;
    
    /**
     * Parse the 'compressed' binary form of Android XML docs
     * such as for AndroidManifest.xml in .apk files
     */
    private static String decompressXML(byte[] bytes_xml)
    {
        StringBuilder stringBuilder = new StringBuilder();
        
        //Compressed XML file/bytes starts with 24x bytes of data,
        //9 32 bit words in little endian order (LSB first):
        //    0th word is 03 00 08 00
        //    3rd word SEEMS TO BE:  Offset at then of StringTable
        //    4th word is: Number of strings in string table
        //WARNING: Sometime I indiscriminently display or refer to word in 
        //little endian storage format, or in integer format (ie MSB first).
        int num_strings = LEW(bytes_xml, 4 * 4);

        //StringIndexTable starts at offset 24x, an array of 32 bit LE offsets
        //of the length/string data in the StringTable.
        int sit_off = 0x24;//Offset of start of StringIndexTable

        //StringTable, each string is represented with a 16 bit little endian 
        //character count, followed by that number of 16 bit (LE) (Unicode) chars.
        int st_off = sit_off + num_strings * 4;//StringTable follows StrIndexTable

        //XMLTags, The XML tag tree starts after some unknown content after the
        //StringTable. There is some unknown data after the StringTable, scan
        //forward from this point to the flag for the start of an XML start tag.
        int xml_tag_off = LEW(bytes_xml, 3 * 4);//Start from the offset in the 3rd word.
        //Scan forward until we find the bytes: 0x02011000(x00100102 in normal int)
        for (int i = xml_tag_off, len = bytes_xml.length - 4; i< len;  i += 4)
        {
            if (LEW(bytes_xml, i) == START_TAG)
            { 
                xml_tag_off = i;
                break;
            }
        }

        //XML tags and attributes:
        //Every XML start and end tag consists of 6 32 bit words:
        //    0th word: 02011000 for startTag and 03011000 for endTag 
        //    1st word: a flag?, like 38000000
        //    2nd word: Line of where this tag appeared in the original source file
        //    3rd word: FFFFFFFF ??
        //    4th word: StringIndex of NameSpace name, or FFFFFFFF for default NS
        //    5th word: StringIndex of Element Name
        //(Note: 01011000 in 0th word means end of XML document, endDocTag)

        //Start tags (not end tags) contain 3 more words:
        //    6th word: 14001400 meaning?? 
        //    7th word: Number of Attributes that follow this tag(follow word 8th)
        //    8th word: 00000000 meaning??

        //Attributes consist of 5 words: 
        //    0th word: StringIndex of Attribute Name's Namespace, or FFFFFFFF
        //    1st word: StringIndex of Attribute Name
        //    2nd word: StringIndex of Attribute Value, or FFFFFFF if ResourceId used
        //    3rd word: Flags?
        //    4th word: str ind of attr value again, or ResourceId of value
        
        stringBuilder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
        
        StringBuffer buffer = new StringBuffer();
        //Step through the XML tree element tags and attributes
        int off = xml_tag_off;
        int indent = 0;
        while (off < bytes_xml.length)
        {
            int tag0 = LEW(bytes_xml, off);
            int name_si = LEW(bytes_xml, off + 5 * 4);

            if (tag0 == START_TAG)
            {
                int num_attrs = LEW(bytes_xml, off + 7 * 4);//Number of Attributes to follow
                off += 9 * 4;//Skip over 6 + 3 words of startTag data
                String name = compXmlString(bytes_xml, sit_off, st_off, name_si);

                //Look for the Attributes
                buffer.setLength(0);
                for (int i = 0; i < num_attrs; ++ i)
                {
                    int attr_name_si = LEW(bytes_xml, off + 1 * 4);//AttrName String Index
                    int attr_value_si = LEW(bytes_xml, off + 2 * 4);//AttrValue Str Ind, or FFFFFFFF
                    int attr_res_id = LEW(bytes_xml, off + 4 * 4);//AttrValue ResourceId or dup AttrValue StrInd
                    off += 5 * 4;//Skip over the 5 words of an attribute

                    String attr_name = compXmlString(bytes_xml, sit_off, st_off, attr_name_si);
                    String attr_value = attr_value_si != -1
                        ? compXmlString(bytes_xml, sit_off, st_off, attr_value_si)
                        : String.valueOf(attr_res_id);
                    buffer.append(String.format(" %1$s=\"%2$s\"", attr_name, attr_value));
                }
                stringBuilder.append("\n");
                stringBuilder.append(String.format("%1$s<%2$s%3$s>", getIndent(indent), name, buffer));
                ++ indent;
            }
            else if (tag0 == END_TAG)
            {
                -- indent;
                off += 6 * 4;//Skip over 6 words of endTag data
                String name = compXmlString(bytes_xml, sit_off, st_off, name_si);
                stringBuilder.append("\n");
                stringBuilder.append(String.format("%1$s</%2$s>", getIndent(indent), name));
            }
            else if (tag0 == END_DOC_TAG)
            {
                break;
            }
        }
        
        return stringBuilder.toString();
    }

    private static String compXmlString(byte[] bytes_xml, int sit_off, int st_off, int str_ind)
    {
        if (str_ind < 0)
        {
            return null;
        }
        int str_off = st_off + LEW(bytes_xml, sit_off + str_ind * 4);
        
        return compXmlStringAt(bytes_xml, str_off);
    }

    private static String getIndent(int indent)
    {
        final String SPACES = "                                             ";
        return SPACES.substring(0, Math.min(indent * 4, SPACES.length()));
    }

    /**
     * Return the string stored in StringTable format at
     * offset strOff.  This offset points to the 16 bit string length, which 
     * is followed by that number of 16 bit (Unicode) chars.
     */
    private static String compXmlStringAt(byte[] arr, int str_off)
    {
        int str_len = arr[str_off + 1] << 8 & 0xff00 | arr[str_off] & 0xff;
        byte[] chars = new byte[str_len];
        for (int i = 0; i < str_len; ++ i)
        {
            chars[i] = arr[str_off + 2 + i * 2];
        }
        return new String(chars);//Hack, just use 8 byte chars
    }

    /**
     * Return value of a Little Endian 32 bit word from the byte array
     * at offset off.
     */
    private static int LEW(byte[] arr, int off)
    {
        return arr[off + 3] << 24 & 0xff000000 | arr[off + 2] << 16 & 0xff0000
            | arr[off + 1] << 8 & 0xff00 | arr[off] & 0xFF;
    }
}
