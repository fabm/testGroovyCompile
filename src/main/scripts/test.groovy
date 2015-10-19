import static groovy.json.StringEscapeUtils.escapeJava
import static groovy.json.StringEscapeUtils.unescapeJava

String escaped = '1234567890 abcdefghijklmnopqrstuvxz olá è\'ã'
String unescaped = unescapeJava(escaped)
itsEscaped = escapeJava(escaped)

println "${unescaped}"
println "${itsEscaped}"