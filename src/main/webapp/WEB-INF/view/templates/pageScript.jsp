<jsp:include page="copyright.jsp" />

<script>
	function saveData(id) {
		console.log('save Data -  ' + id)
		var fname = $('#text_fname_' + id).val();
		var lname = $('#text_lname_' + id).val();
		if (fname == "") {
			$('#text_fname_' + id).css('border-color', 'red');
			return;
		}
		if (lname == "") {
			$('#text_lname_' + id).css('border-color', 'red');
			return;
		}
		$.ajax({
			type : "POST",
			url : "/save",
			contentType : "application/json",
			dataType : "json",
			data : JSON.stringify({
				id : id,
				firstName : fname,
				lastName : lname
			}),
			success : function(data, textStatus, xhr) {
				console.log("success  ---> ");
				window.location = "/";

			},
			error : function(data, xhr, textStatus) {
				console.log("failure ---> ");
				console.log(JSON.stringify(xhr));
			}
		});

	}

	function hideContent() {
		$('#loadingDiv').show();
		$('#contentDiv').hide();
	}

	function showContent() {
		$('#loadingDiv').hide();
		$('#contentDiv').show();
	}
</script>
<jsp:include page="footer.jsp" />
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
<link rel="stylesheet"
	href="https://cdn.datatables.net/1.10.12/css/jquery.dataTables.min.css">
<script
	src="https://cdn.datatables.net/1.10.12/js/jquery.dataTables.min.js"></script>
	<script
	src="https://code.jquery.com/jquery-3.3.1.js"></script>
<script
	src="https://cdn.datatables.net/buttons/1.5.6/js/dataTables.buttons.min.js"></script>
<script
	src="https://cdn.datatables.net/select/1.3.0/js/dataTables.select.min.js"></script>

<script src="resources/js/edit.js"></script>
<script src="resources/js/home.js"></script>
<script src="resources/js/common/jquery.spring-friendly.js"></script>
