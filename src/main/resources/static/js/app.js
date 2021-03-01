$(function () {

    $("a.confirmDeletion").click(function () {
        if(!confirm("Confirm deletion")) return false;
    });

    if($("#content").length) {
        ClassicEditor.create(document.querySelector("#content"))
            .catch(err => {
                console.log(err);
            });
    }

    if($("#descrip").length) {
        ClassicEditor.create(document.querySelector("#descrip"))
            .catch(err => {
                console.log(err);
            });
    }
    
});

function readUrl(input, idNum) {

    if (input.files && input.files[0]) {
        let reader = new FileReader();

        reader.onload = function (e) {
            $("#imgPreview" + idNum).attr("src", e.target.result).width(100).height(100);
        }

        reader.readAsDataURL(input.files[0]);
    }

}