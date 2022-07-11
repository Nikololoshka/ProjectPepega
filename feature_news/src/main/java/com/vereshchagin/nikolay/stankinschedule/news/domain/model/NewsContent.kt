package com.vereshchagin.nikolay.stankinschedule.news.domain.model

data class NewsContent(
    val id: Int,
    val date: String,
    val title: String,
    val previewImageUrl: String,
    val text: String,
    val deltaFormat: String,
) {

    fun prepareQuillPage(): String = """
        <!DOCTYPE html>
        <html lang="ru">
            <head>
                <meta charset='UTF-8'>
                <meta name='viewport' content='width=device-width, initial-scale=1'>
                <link rel="stylesheet" href="file:///android_asset/news/quill.css">         
                <script src="file:///android_asset/news/quill.min.js" type="text/javascript"></script>
            </head>
            <body>
                <div id="raw-text"> $text </div>
                <div id="editor" style="display: none;"></div>
                <div id="viewer"></div>        
                <script>
                    var delta = $deltaFormat
                    var quill = new Quill('#editor', { readOnly: true });
                    try {
                        quill.setContents(delta);
                    } catch (error) {
                        console.error(error);
                    }
                    document.getElementById("viewer").innerHTML = quill.root.innerHTML;
                    document.getElementById("editor").remove();            
                    Android.onNewsLoaded();          
                </script>
            </body>
        </html>
    """.trimIndent()
}