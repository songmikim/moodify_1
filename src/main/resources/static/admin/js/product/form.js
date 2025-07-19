window.addEventListener("DOMContentLoaded", function() {
    // 에디터 로드
    const { editor } = commonLib;
    const gid = document.querySelector("input[name='gid']").value;
    editor.load("#description", gid, "editor", (quill) => {
        window.quill = quill;
    });


    // 업로드된 이미지 삭제 처리
    const { fileManager } = commonLib;

    const removeEls = document.querySelectorAll(".uploaded_images .remove");
    removeEls.forEach(el => {
        el.addEventListener("click", function() {
            const { seq } = this.dataset;
            if (confirm("정말 삭제하겠습니까?")) {
                fileManager.delete(seq);
            }
        });
    })
});

/**
* 파일 업로드 후속 처리
*
*/
function fileUploadCallback(items) {
    if (!items || items.length === 0) return;

    const { fileManager } = commonLib;

    console.log("items", items);

    // 메인 이미지
    const mainTarget = document.getElementById("main-images");

    // 목록 이미지
    const listTarget = document.getElementById("list-images");
    listTarget.innerHTML = "";

    // 에디터 이미지
    const editorTarget = document.getElementById("editor-images");

    // 이미지 업로드 시 치환될 템플릿
    const imageTpl = document.getElementById("image-tpl").innerHTML;
    const editorTpl = document.getElementById("editor-tpl").innerHTML;

    const domParser = new DOMParser();

    for (const item of items) {
        let targetEl = null;
        const { seq, fileName, fileUrl } = item;
        let html = item.location === 'editor' ? editorTpl : imageTpl;

        switch (item.location) {
            case "main": // 메인 이미지
                targetEl = mainTarget;
                break;
            case "list": // 목록 이미지
                targetEl = listTarget;
                break;
            case "editor": // 에디터 이미지
                targetEl = editorTarget;

                // 업로드한 이미지를 에디터 첨부
                if (quill) {
                    const selIndex = quill.getSelection().index;
                    quill.insertEmbed(selIndex, "image", fileUrl);
                }
                break;
        }



        let baseUrl = document.querySelector("meta[name='base_url']").content;
        baseUrl = baseUrl && baseUrl.lastIndexOf("/") !== -1 ? baseUrl.substring(0, baseUrl.lastIndexOf("/")) : baseUrl;

        let imageUrl = `${baseUrl}/file/thumb?seq=${seq}&width=200&height=200&crop=true`;

        html = html.replace(/\[seq\]/g, seq)
                    .replace(/\[imageUrl\]/g, imageUrl)
                    .replace(/\[fileUrl\]/g, fileUrl)
                    .replace(/\[fileName\]/g, fileName);

        const dom = domParser.parseFromString(html, "text/html");
        const el = dom.querySelector(item.location === 'editor' ? "span" : "div");
        targetEl.append(el);

        // 삭제 처리 버튼 처리
        const removeEl = el.querySelector(".remove");
        if (removeEl) {
            removeEl.addEventListener("click", function() {
                if (confirm("정말 삭제하겠습니까?")) {
                    fileManager.delete(seq);
                }
            });
        }

        // 전체 이미지 보기 클릭 처리
        const showImageEl = el.querySelector(".show-image");
        if (showImageEl) {
            showImageEl.addEventListener("click", function() {
                const { seq } = showImageEl.dataset;
                fileManager.showImage(seq);
            });

        }
    }
}


/**
* 파일 삭제 성공시에 후속처리
*
*/
function fileDeleteCallback({seq}) {
    const el = document.getElementById(`file-${seq}`);
    if (el) {
        el.parentElement.removeChild(el);
    }
}