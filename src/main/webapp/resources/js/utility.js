$(function() {
	$("#selectMonth").addClass("ui-widget ui-jqdialog");
	$("#selectMonth").select2();
	$("#success-alert").hide();
	$("#revertUtility").attr("disabled", "disabled");
	$("#runUtility").attr("disabled", "disabled");
	
	$("#selectMonth").change(function() {
		if($(this).val() === "") {
			$("#revertUtility").attr("disabled", "disabled");
			$("#runUtility").attr("disabled", "disabled");
		} else {
			$("#revertUtility").removeAttr("disabled", "disabled");
			$("#runUtility").removeAttr("disabled", "disabled");
		}
	});
	
	$("#runUtility").click(function() {
		$.ajax({
			url : "runUtility?month="+$("#selectMonth").val(),
			success : function(result) {
				$("#success-alert").fadeTo(2000, 500).slideUp(500, function() {
			      $("#success-alert").slideUp(500);
			    });
			}
		});
	});
	
	$("#revertUtility").click(function() {
		$.ajax({
			url : "revertUtility?month="+$("#selectMonth").val(),
			success : function(result) {
				$("#success-alert").fadeTo(2000, 500).slideUp(500, function() {
			      $("#success-alert").slideUp(500);
			    });
			}
		});
	});
});
