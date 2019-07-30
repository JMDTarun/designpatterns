$(function() {
	$("#success-alert").hide();
	
	$("#saveCredentials").click(function() {

		$.ajax({
		    type: "POST",
			url : "manageCredentials",
			data: JSON.stringify({
			    username: $("#fastwayUsername").val(),
			    password: $("#fastwayPassword").val()
			}),
			contentType: "application/json",
			accept: "application/json",
			beforeSend: function() {
			    $("#saveCredentials").attr("disabled", "disabled");
			},
			complete: function(){
			    $("#saveCredentials").attr("disabled", "false");
			    },
			success : function(result) {
				$("#success-alert").fadeTo(2000, 500).slideUp(500, function() {
			      $("#success-alert").slideUp(500);
			    });
			}
		});
	});
});
