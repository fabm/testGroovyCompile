import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook


String resPath = project.build.resources.directory[0]

Map<String, Tuple2<Integer, Integer>> langs = [
        'en': [[0, 1]],
        'fr': [[0, 2]]
]

Closure<Void> excelRead = { file ->
    Map<String, Writer> writers = [:]
    int maxRow = 2;

    Closure<String> rowEval = { ->
        (0..maxRow).each { int i ->
            columns.each { def pair ->
                try {
                    String key = sheet.getRow(i).getCell(pair[0])
                    String value = sheet.getRow(i).getCell(pair[1])
                    writer.write "$key=$value\n"
                } catch (NullPointerException e) {
                }
            }
        }
    }

    Closure<?> sheetEval = { sheet ->
        writers.each { elWriter ->
            Writer writer = elWriter.value
            def columns = langs[elWriter.key]
            rowEval.setProperty('sheet', sheet)
            rowEval.setProperty('columns', columns)
            rowEval.setProperty('writer', writer)
            rowEval()
        }
    }

    List<String> keys = langs.keySet().asList()
    Closure<?> openSheet
    openSheet = { ->
        if (keys.empty) {
            XSSFSheet sheet = new XSSFWorkbook(file).getSheet('Sheet1')
            sheetEval(sheet)
        } else {
            String currentLanguage = keys.pop()
            new File("${resPath}/translations_${currentLanguage}.properties").withWriter { writer ->
                writers[currentLanguage] = writer
                openSheet()
            }
        }
    }

    openSheet()

}
new FileInputStream("${project.basedir}/src/main/scripts/teste.xlsx").withCloseable excelRead