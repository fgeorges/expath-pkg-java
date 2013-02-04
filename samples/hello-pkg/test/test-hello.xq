import module namespace h = "http://www.example.org/hello";

declare namespace saxon="http://saxon.sf.net/";
declare option saxon:output "omit-xml-declaration=yes";

<greetings> {
  h:hello('world')
}
</greetings>
