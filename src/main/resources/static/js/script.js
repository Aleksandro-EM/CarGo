$(document).ready(function () {
    $("#table").DataTable({
        'columnDefs': [{
            'orderable': false,
            'targets': [-1]
        }]
    });
});

const DEFAULT_IMAGE_URL = document.getElementById('imageUrl').options[0].value;

const fileInput = document.getElementById('imageFile');
const selectInput = document.getElementById('imageUrl');
const preview = document.getElementById('imagePreview');

fileInput.addEventListener('change', () => {
    const file = fileInput.files[0];
    if(file) {
        const reader = new FileReader();
        reader.onload = e => preview.src = e.target.result;
        reader.readAsDataURL(file);

        selectInput.value = DEFAULT_IMAGE_URL;
    }
});

selectInput.addEventListener('change', () => {
    if(selectInput.value === DEFAULT_IMAGE_URL || !selectInput.value) {
        preview.src = DEFAULT_IMAGE_URL;
    } else {
        preview.src = selectInput.value;
    }

    fileInput.value = '';
});

