// Designing methods for complex data

import tester.*;

// represents a list of documents
interface ILoXMLFrag {
  
}

// represents an empty list of documents
class MtLoXMLFrag implements ILoXMLFrag {
  
}

// represents a list of documents with at least one XMLFrag
class ConsLoXMLFrag implements ILoXMLFrag {
  IXMLFrag first;
  ILoXMLFrag document;
  
  // the constructor
  ConsLoXMLFrag(IXMLFrag first, ILoXMLFrag document) {
    this.first = first;
    this.document = document;
  }
}

// represents a document
interface IXMLFrag {
  
}

// represents a document with just plaintext
class Plaintext implements IXMLFrag {
  String txt;
  
  Plaintext(String txt) {
    this.txt = txt;
  }
}

// represents a document with at least one Tag
class Tagged implements IXMLFrag {
  Tag tag;
  ILoXMLFrag content;
  
  Tagged(Tag tag, ILoXMLFrag content) {
    this.tag = tag;
    this.content = content;
  }
}

// represents a Tag with a name and a List of Attributes
class Tag {
  String name;
  ILoAtt atts;
  
  Tag(String name, ILoAtt atts) {
    this.name = name;
    this.atts = atts;
  }
}

// represents a list of Attributes
interface ILoAtt {
  
}

// represents an empty list of Attributes 
class MtLoAtt implements ILoAtt {
  
}

// represents a list of Attributes with at least one Attribute
class ConsLoAtt implements ILoAtt {
  Att att;
  ILoAtt rest;
  
  ConsLoAtt(Att att, ILoAtt rest) {
    this.att = att;
    this.rest = rest;
  }
}

// represents an Attribute
class Att {
  String name;
  String value;
  
  Att(String name, String value) {
    this.name = name;
    this.value = value;
  }
}



// examples of XML
class ExamplesXML {
  Att volumeAtt = new Att("volume", "30db");
  Att durationAtt = new Att("duration", "5sec");

  

  Tag yellTag = new Tag("yell", new MtLoAtt());
  Tag italicTag = new Tag("italic", new MtLoAtt());
  Tag yell2Tag = new Tag("yell", new ConsLoAtt(this.volumeAtt, new MtLoAtt()));
  Tag yell3Tag = new Tag("yell", new ConsLoAtt(this.volumeAtt, new ConsLoAtt(this.durationAtt, new MtLoAtt())));
}
