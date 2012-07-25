(function () {
	var input = document.getElementById("myFile"), 
		formdata = false;

	if (window.FormData) {
  		formdata = new FormData();
  		document.getElementById("btn").style.display = "none";
	}

 	input.addEventListener("change", function (evt) {
 		
 		var len = this.files.length, reader, file;

 		if(len >0)
 			{
 			file = this.files[0];
			if (!!file.type.match(/image.*/)) {
				if ( window.FileReader ) {
					reader = new FileReader();
					reader.onloadend = function (e) { 
					};
					reader.readAsDataURL(file);
				}
				if (formdata) {
					formdata.append("image", file);
				}
			}	
 			}
 		
		if (formdata) {
			$.ajax({
				url: "/dispatch",
				type: "POST",
				processData: false,
				contentType: false,
				success: function (res) {
					$.ajax({
						url: res,
						type: "POST",
						data: formdata,
						processData: false,
						contentType: false,
						success: function (res) {
							document.getElementById("blobkey").value = res; 
						}
					});
					//document.getElementById("blobkey").value = res; 
				},
				error : function(res) {
					alert("error response recieved " + res);
				}
			});
		}
	}, false);
}());