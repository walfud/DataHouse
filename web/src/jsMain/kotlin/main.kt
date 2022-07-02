import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.get

fun main() {
    val body = document.getElementsByTagName("body")[0] as HTMLElement

    // Enabling keyboard control
    body.addEventListener("keyup", {
        when ((it as KeyboardEvent).keyCode) {
            38 -> { // Arrow up
                console.log("up")
            }
        }
    })

    renderComposable(rootElementId = "root") {

        Div(
            attrs = {
                style {
                    property("text-align", "center")
                }
            }
        ) {

           Text("foooo")
        }

    }
}