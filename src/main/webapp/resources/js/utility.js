$(function() {
	$("#selectMonth").addClass("ui-widget ui-jqdialog");
	$("#selectMonth").select2();
	$("#runUtility").click(function() {
		$.ajax({
			url : "runUtility?month="+$("#selectMonth").val(),
			success : function(result) {
				alert("Done");
			}
		});
	});
});
