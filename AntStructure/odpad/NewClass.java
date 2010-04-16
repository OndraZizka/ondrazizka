import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import org.w3c.dom.Document;

public class Dom02 {

  public static void main(String argv[]) {
    if (argv.length != 2) {
      System.err.println(
        "usage: java Dom02 fileIn fileOut");
      System.exit(0);
    }//end if

    try{
      //Get a factory object for DocumentBuilder
      // objects
      DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();

      //Configure the factory object
      factory.setValidating(false);
      factory.setNamespaceAware(false);

      //Get a DocumentBuilder (parser) object
      DocumentBuilder builder =
                    factory.newDocumentBuilder();

      //Parse the XML input file to create a
      // Document object that represents the
      // input XML file.
      Document document = builder.parse(
                              new File(argv[0]));

      //Use an anonymous object of the
      // Dom02Writer class to traverse the
      // Document object, extracting information
      // about each of the nodes, and using that
      // information to write an output XML
      // file that represents the Document
      // object.
      new Dom02Writer(argv[1]).
                          writeXmlFile(document);
    }catch(Exception e){
      e.printStackTrace(System.err);
    }//end catch

  }// end main()
} // class Dom02

Listing 28

/*File Dom02Writer.java
Copyright 2003 R.G.Baldwin

This class provides a utility method named
writeXmlFile() that receives a DOM Document
object as a parameter and writes an output XML
file that matches the information contained in
the Document object.

The output file is created by recursively
traversing the Document object, identifying each
of the nodes in that object, and converting each
node to text in an XML format.

No effort is made to insert spaces and line
breaks to make the output cosmetically pleasing.
Also, nothing is done to eliminate cosmetic
whitespace that may exist in the Document object.

The name of the XML file is established as a
parameter to the constructor for the class.

A cosmetically pleasing view of the output file
can be obtained by opening the output file in
IE 5.0 or later.

Briefly tested using JDK 1.4.2 and WinXP.  Note
however that this class has not been thoroughly
tested. If you use it for a critical application,
test it thoroughly before using it.
************************************************/

import java.io.PrintWriter;
import java.io.FileOutputStream;

import org.w3c.dom.*;

public class Dom02Writer {
  private PrintWriter out;

  //-------------------------------------------//

  public Dom02Writer(String xmlFile) {
    try {
      out = new PrintWriter(
                  new FileOutputStream(xmlFile));
    }catch (Exception e) {
      e.printStackTrace(System.err);
    }//end catch
  }//end constructor
  //-------------------------------------------//

  //This method converts an incoming Document
  // object to an output XML file
  public void writeXmlFile(Document document){
    try {
      //Write the contents of the Document object
      // into an ontput file in XML file format.
      writeNode(document);
    }catch (Exception e) {
      e.printStackTrace(System.err);
    }//end catch
  }//end writeXmlFile()
  //-------------------------------------------//

  //This method is used recursively to convert
  // node data to XML format and to write the XML
  // format data to the output file.
  public void writeNode(Node node) {
    if (node == null) {
      System.err.println(
                  "Nothing to do, node is null");
      return;
    }//end if

    //Process the node based on its type.
    int type = node.getNodeType();

    switch (type) {
      //Process the Document node
      case Node.DOCUMENT_NODE: {
        //Write a required line for an XML
        // document.
        out.print("<?xml version=\"1.0\"?>");

        //Get and write the root element of the
        // Document.  Note that this is a
        // recursive call.
        writeNode(
          ((Document)node).getDocumentElement());
        out.flush();
        break;
      }//end case Node.DOCUMENT_NODE

      //Write an element with attributes
      case Node.ELEMENT_NODE: {
        out.print('<');//begin the start tag
        out.print(node.getNodeName());

        //Get and write the attributes belonging
        // to the element.  First get the
        // attributes in the form of an array.
        Attr attrs[] = getAttrArray(
                           node.getAttributes());

        //Now process all of the attributes in
        // the array.
        for (int i = 0; i < attrs.length; i++){
          Attr attr = attrs[i];
          out.print(' ');//write a space
          out.print(attr.getNodeName());
          out.print("=\"");//write ="
          //Convert <,>,&, and quotation char to
          // entities and write the text
          // containing the entities.
          out.print(
                  strToXML(attr.getNodeValue()));
          out.print('"');//write closing quote
        }//end for loop
        out.print('>');//write end of start tag

        //Deal with the possibility that there
        // may be other elements nested in this
        // element.
        NodeList children = node.getChildNodes();
        if (children != null) {//nested elements
          int len = children.getLength();
          //Iterate on NodeList of child nodes.
          for (int i = 0; i < len; i++) {
          //Write each of the nested elements
          // recursively.
          writeNode(children.item(i));
          }//end for loop
        }//end if
        break;
      }//end case Node.ELEMENT_NODE

      //Handle entity reference nodes
      case Node.ENTITY_REFERENCE_NODE:{
        out.print('&');
        out.print(node.getNodeName());
        out.print(';');
        break;
      }//end case Node.ENTITY_REFERENCE_NODE

      //Handle text
      case Node.CDATA_SECTION_NODE:
      case Node.TEXT_NODE: {
        //Eliminate <,>,& and quotation marks and
        // write to output file.
        out.print(strToXML(node.getNodeValue()));
        break;
      }//end case Node.TEXT_NODE

      //Handle processing instruction
      case Node.PROCESSING_INSTRUCTION_NODE:{
        out.print("<?");
        out.print(node.getNodeName());
        String data = node.getNodeValue();
        if (data != null && data.length() > 0){
          out.print(' ');//write space
          out.print(data);
        }//end if
        out.print("?>");
        break;
      }//end Node.PROCESSING_INSTRUCTION_NODE
    }//end switch

    //Now write the end tag for element nodes
    if (type == Node.ELEMENT_NODE) {
      out.print("</");
      out.print(node.getNodeName());
      out.print('>');

    }//end if

  }//end writeNode(Node)
  //-------------------------------------------//

  //The following methods are utility methods

  //This method inserts entities in place
  // of <,>,&, and quotation mark
  private String strToXML(String s) {
    StringBuffer str = new StringBuffer();

    int len = (s != null) ? s.length() : 0;

    for (int i = 0; i < len; i++) {
      char ch = s.charAt(i);
      switch (ch) {
        case '<': {
          str.append("&lt;");
          break;
        }//end case '<'
        case '>': {
          str.append("&gt;");
          break;
        }//end case '>'
        case '&': {
          str.append("&amp;");
          break;
        }//end case '&'
        case '"': {
          str.append("&quot;");
          break;
        }//end case '"'
        default: {
          str.append(ch);
        }//end default
      }//end switch
    }//end for loop

    return str.toString();

  }//end strToXML()
  //-------------------------------------------//

  //This method converts a NamedNodeMap into an
  // array of type Attr
  private Attr[] getAttrArray(
                             NamedNodeMap attrs){
    int len = (attrs != null) ?
                           attrs.getLength() : 0;
    Attr array[] = new Attr[len];
    for (int i = 0; i < len; i++) {
      array[i] = (Attr)attrs.item(i);
    }//end for loop

    return array;
  }//end getAttrArray()

  //-------------------------------------------//

} // end class Dom02Writer