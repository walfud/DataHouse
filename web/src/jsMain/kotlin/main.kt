import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.web.attributes.ATarget
import org.jetbrains.compose.web.attributes.target
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.jetbrains.compose.web.renderComposable
import kotlin.random.Random

fun main() {
    renderComposable(rootElementId = "root") {
        App()
    }
}

val TITLE = listOf(
    "五分钟零基础搞懂Hadoop",
    "五分钟零基础介绍 Spark",
    "5分钟深入浅出 HDFS",
    "三分钟入门大数据之Hive与HBase之间的区别与联系是什么？",
    "三分钟了解下大数据技术发展史",
    "Hive 性能调优的 8 种方式，你了解吗？",
    "什么是MapReduce(入门篇)",
    "Spark入门系列（一） | 30分钟理解Spark的基本原理",
    "从一无所知到5分钟快速了解HBase",
    "三分钟了解下HDFS 是如何实现大数据高容量、可靠的存储和访问的。",
    "容易理解的Hbase架构全解，10分钟学会",
)
val URL = listOf(
    "https://zhuanlan.zhihu.com/p/20176725",
    "https://zhuanlan.zhihu.com/p/269557077",
    "https://zhuanlan.zhihu.com/p/20267586",
    "https://baijiahao.baidu.com/s?id=1644971049290715328&wfr=spider&for=pc",
    "https://blog.csdn.net/pop_xiaohao/article/details/119811385",
    "https://zhuanlan.zhihu.com/p/80718835",
    "https://cloud.tencent.com/developer/article/1778549",
    "https://blog.csdn.net/dQCFKyQDXYm3F8rB0/article/details/95937336",
    "https://blog.51cto.com/u_15270048/3290359",
    "https://zhuanlan.zhihu.com/p/403783330",
    "https://blog.csdn.net/weixin_43392489/article/details/103020632",
)

val client = HttpClient {
    install(JsonFeature) { serializer = KotlinxSerializer() }
}

@Composable
private fun App() {
    val datas = remember {
        mutableStateListOf<Data>()
    }

    Div(
        attrs = {
            style {
                property("width", "100%")
                property("height", "100%")
                display(DisplayStyle.Flex)
                flexDirection(FlexDirection.Column)
                justifyContent(JustifyContent.Center)
                alignItems(AlignItems.Center)
            }
        }
    ) {
        H1 {
            Text("walfud's 从埋点到数仓探索 \uD83E\uDD9D")
        }

        Div(
            attrs = {
                style {
                    width(500.px)
                    marginTop(100.px)

                    display(DisplayStyle.Flex)
                    flexDirection(FlexDirection.Row)
                    justifyContent(JustifyContent.SpaceBetween)
                    alignItems(AlignItems.Center)
                }
            }
        ) {
            Div(
                attrs = {
                    style {
                        width(80.px)
                        display(DisplayStyle.Flex)
                        flexDirection(FlexDirection.Column)
                        justifyContent(JustifyContent.SpaceEvenly)
                        alignItems(AlignItems.Center)
                    }
                }
            ) {
                Button(
                    attrs = {
                        onClick {
                            val dataIndex = datas.size
                            val titleIndex = Random.nextInt(TITLE.size)
                            val newData = Data(
                                dataIndex,
                                TITLE[titleIndex],
                                URL[titleIndex],
                                1,
                            )
                            datas.add(newData)

                            GlobalScope.launch {
                                var resultStatus = 3
                                try {
                                    client.post<Data>("http://localhost:8080/log") {
                                        contentType(ContentType.Application.Json)
                                        body = newData
                                    }
                                } catch (e: Exception) {
                                    resultStatus = 2
                                    e.printStackTrace()
                                }

                                datas.removeAt(dataIndex)
                                val resultData = Data(
                                    newData.id,
                                    newData.title,
                                    newData.url,
                                    resultStatus,
                                )
                                datas.add(dataIndex, resultData)
                            }
                        }
                    }
                ) {
                    Text("点击触发埋点")
                }
            }

            Ol(
                attrs = {
                    attr("reversed", "reversed")
                    style {
                        property("width", "300px")
                        property("height", "400px")

                        border {
                            borderWidth(1.px)
                            style = LineStyle.Solid
                        }

                        overflow("scroll")
                    }
                }
            ) {
                datas.reversed().map {
                    Li {
                        Div(
                            attrs = {
                                style {
                                    height(60.px)
                                    padding(5.px)
                                    marginBottom(5.px)

                                    display(DisplayStyle.Flex)
                                    flexDirection(FlexDirection.Row)
                                    justifyContent(JustifyContent.Start)
                                    alignItems(AlignItems.Center)

                                    border {
                                        borderWidth(1.px)
                                        style = LineStyle.Dashed
                                        color = it.statusToColor()
                                    }

                                    overflow("hidden")
                                }
                            }
                        ) {
                            Label(
                                attrs = {
                                    style {
                                        width(60.px)
                                        color(it.statusToColor())
                                        textAlign("center")
                                    }
                                }
                            ) {
                                Text(it.statusToString())
                            }
                            Div(
                                attrs = {
                                    style {
                                        width(1.px)
                                        height(50.px)
                                        marginLeft(5.px)
                                        marginRight(5.px)

                                        backgroundColor(Color.black)
                                    }
                                }
                            )
                            A(
                                it.url,
                                attrs = {
                                    target(ATarget.Blank)
                                }) {
                                Text(it.title)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Serializable
data class Data(
    val id: Int,
    val title: String,
    val url: String,
    val status: Int,     // 1: sending, 2: failed, 3: ok
) {
    fun statusToColor(): CSSColorValue = when (status) {
        1 -> Color.blue
        2 -> Color.red
        3 -> Color.green
        else -> Color.black
    }

    fun statusToString(): String = when (status) {
        1 -> "seding"
        2 -> "failed"
        3 -> "ok"
        else -> "unknown"
    }
}