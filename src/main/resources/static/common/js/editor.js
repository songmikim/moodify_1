var commonLib = commonLib ?? {}

commonLib.editor = {
    /**
    * 에디터 로드
    *
    */
    load(selector, gid, location, callback) {
        const toolbarOptions = [
          ['bold', 'italic', 'underline', 'strike'],        // toggled buttons
          ['blockquote', 'code-block'],
          ['link', 'image', 'video', 'formula'],

          [{ 'header': 1 }, { 'header': 2 }],               // custom button values
          [{ 'list': 'ordered'}, { 'list': 'bullet' }, { 'list': 'check' }],
          [{ 'script': 'sub'}, { 'script': 'super' }],      // superscript/subscript
          [{ 'indent': '-1'}, { 'indent': '+1' }],          // outdent/indent
          [{ 'direction': 'rtl' }],                         // text direction

          [{ 'size': ['small', false, 'large', 'huge'] }],  // custom dropdown
          [{ 'header': [1, 2, 3, 4, 5, 6, false] }],

          [{ 'color': [] }, { 'background': [] }],          // dropdown with defaults from theme
          [{ 'font': [] }],
          [{ 'align': [] }],

          ['clean']                                         // remove formatting button
        ];

        const quill = new Quill(selector, {
            modules: {
                toolbar: toolbarOptions
            },
            theme: 'snow',
         });

         /* 툴바 이미지 버튼 클릭시 처리 */
         const { fileManager } = commonLib;
         const toolbar = quill.getModule('toolbar');
         toolbar.addHandler('image', function() {
            const fileEl = document.createElement("input");
            fileEl.type = 'file';
            fileEl.multiple = true;
            fileEl.accept = 'image/*';
            fileEl.click();


            fileEl.addEventListener("change", function() {
                const files = this.files;
                fileManager.upload(files, gid, location, true);
            });
         });

        if (typeof callback === 'function') {
            callback(quill);
        }
    },
    /**
    * 에디터 이미지 추가
    *
    */
    insertImage(quill, imageUrl) {
        console.log(imageUrl);
    }
}