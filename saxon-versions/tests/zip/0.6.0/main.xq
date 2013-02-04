import module namespace zip = "http://expath.org/ns/zip";

declare namespace saxon = "http://saxon.sf.net/";
declare option saxon:output "indent=yes";

<result>
   <binary-entry> {
      zip:binary-entry('../test.zip', 'test.txt')
   }
   </binary-entry>
   <html-entry> {
      zip:html-entry('../test.zip', 'sub/test.html')
   }
   </html-entry>
   <text-entry> {
      zip:text-entry('../test.zip', 'test.txt')
   }
   </text-entry>
   <xml-entry> {
      zip:xml-entry('../test.zip', 'test.xml')
   }
   </xml-entry>
   <entries> {
      zip:entries('../test.zip')
   }
   </entries>
</result>
