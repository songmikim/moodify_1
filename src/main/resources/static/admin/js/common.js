window.addEventListener("DOMContentLoaded", function() {
    // 전체 토글 공통 기능 처리 S
    const chkAlls = document.getElementsByClassName("check-all");
    for (const el of chkAlls) {
        el.addEventListener("click", function() {
            const { targetName } = this.dataset;
            const chks = document.getElementsByName(targetName);
            for (const chk of chks) {
                chk.checked = this.checked;
            }
        });
    }
    // 전체 토글 공통 기능 처리 E

    // 공통 양식 처리 S
    const processFormButtons = document.getElementsByClassName("process-form");
    for (const el of processFormButtons) {
        el.addEventListener("click", function() {
            const method = this.classList.contains("delete") ? "DELETE" : "PATCH";
            const { formName } = this.dataset;
            const formEl = document[formName];
            formEl._method.value = method;
            alert('정말 처리하겠습니까?', () => formEl.submit());
        });
    }
    // 공통 양식 처리 E
});