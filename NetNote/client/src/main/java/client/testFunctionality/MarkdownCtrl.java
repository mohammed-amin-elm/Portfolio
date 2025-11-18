package client.testFunctionality;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;


/**
 * The Markdown Controller Class is responsible for fetching text from the TextArea
 * and rendering the text in markdown on a WebView object.
 */
public class MarkdownCtrl {
    @FXML
    private TextArea markdownInput;

    @FXML
    private WebView htmlOutput;

    private final Parser markdownParser = Parser.builder().build();
    private final HtmlRenderer htmlRenderer = HtmlRenderer.builder().build();

    /**
     * The renderMarkdown method takes in a string and parses the string from markdown to html.
     * Then it renders the html on a WebView object.
     * @param markdown The markdown string to parse
     */
    private void renderMarkdown(String markdown) {
        String html = htmlRenderer.render(markdownParser.parse(markdown));
        WebEngine webEngine = htmlOutput.getEngine();
        webEngine.loadContent(html);
    }

    /**
     * This method is executed everytime a key is typed on the TextArea object. The entire text
     * inside the TextArea is the rendered on the WebView.
     */
    public void onTextAreaKeyTyped() {
        renderMarkdown(markdownInput.getText());
    }
}
