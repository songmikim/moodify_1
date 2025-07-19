var commonLib = commonLib ?? {}

commonLib.fileManager = {
    /**
    * 파일 업로드
    *
    * @param files : 업로드한 파일 정보
    * @param gid : 그룹 ID
    * @param location : 그룹 내에서 위치명
    * @param imageOnly : 이미지로 파일 형식을 제한
    * @param single : 단일 파일 업로드
    */
    upload(files, gid, location, imageOnly, single) {
        try {
            // 파일 업로드 여부 체크
            if (!files || files.length === 0) {
                throw new Error("파일을 업로드 하세요.")
            }

            // 이미지 형식의 파일로 제한
            if (imageOnly) {
                for (const file of files) {
                    // 이미지가 아닌 파일이 포함된 경우
                    if (!file.type.startsWith("image/")) {
                        throw new Error("이미지 형식의 파일만 업로드 하세요.")
                    }
                }
            }

            // gid 필수 여부 체크
            if (!gid || !('' +gid).trim()) {
                throw new Error("잘못된 접근입니다.");
            }

            // 파일 업로드 양식 동적 생성
            const formData = new FormData();
            for (const file of files) {
                formData.append("file", file);
            }

            formData.append("gid", gid);
            if (location && ('' + location).trim()) {
                formData.append("location", location);
            }

            formData.append("single", Boolean(single));
            formData.append("imageOnly", Boolean(imageOnly))

            // ajax로 파일 업로드 요청 처리
            const { ajaxLoad } = commonLib;
            ajaxLoad('/file/upload',(items) => {
               // 성공시 후속 처리
               if (typeof fileUploadCallback === 'function') {
                    fileUploadCallback(items);
               }
            }, (err) => {
                // 실패시 후속 처리
                alert('파일 업로드에 실패하였습니다.');
                console.error(err);
            }, 'POST', formData)

        } catch (err) {
            console.error(err);
            alert(err.message);
        }
    },
    /**
    * 파일 삭제
    *
    */
    delete(seq) {
        const { ajaxLoad } = commonLib;

        ajaxLoad(`/file/delete/${seq}`,
        (item) => {
            if (typeof fileDeleteCallback === 'function') {
                fileDeleteCallback(item);
            }
        },
        (err) => {
            alert('파일삭제 실패하였습니다.');
            console.error(err);
        }, 'DELETE')
    },
    /**
    * 원본 이미지 출력
    *
    */
    showImage(seq) {
        const url = commonLib.getUrl(`/file/image/${seq}`);
        open(url);
    }
};

window.addEventListener("DOMContentLoaded", function() {
    const fileUploadButtons = document.getElementsByClassName("file-upload-btn");

    // 동적으로 input[type='file'] 형태인 Document 객체를 생성
    const fileEl = document.createElement("input");
    fileEl.type = 'file';

    for (const el of fileUploadButtons) {
        el.addEventListener("click", function() { // 파일 업로드 버튼을 클릭한 경우
            const { gid, location, single, imageOnly } = this.dataset;

            fileEl.gid = gid;
            fileEl.location = location;
            fileEl.multiple = single !== 'true';
            fileEl.imageOnly = imageOnly === 'true';
            fileEl.single = single === 'true';

            fileEl.click(); // 파일 탐색기 열기

        });
    }

    /* 파일 선택시 처리 S */
    fileEl.addEventListener("change", function() {
        const files = this.files;
        const { gid, location, single, imageOnly } = fileEl;

        const { fileManager } = commonLib;

        fileManager.upload(files, gid, location, imageOnly, single);

    });
    /* 파일 선택시 처리 E */
});