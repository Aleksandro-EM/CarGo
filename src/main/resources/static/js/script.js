$(document).ready(function () {
    $("#table").DataTable({
        'columnDefs': [{
            'orderable': false,
            'targets': [-1]
        }]
    });
});

function previewFile(input) {
    const preview = document.getElementById('imagePreview');
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function (e) {
            preview.src = e.target.result;
        }
        reader.readAsDataURL(input.files[0]);
    }
}

function selectExistingImage(select) {
    const preview = document.getElementById('imagePreview');
    preview.src = select.value;
}

