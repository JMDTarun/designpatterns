var editor;
$(document).ready(function() {
	
	editor = new $.fn.dataTable.Editor( {
        ajax: "/data/users",
        table: "#employeesTable",
        fields: [ {
                label: "First name:",
                name: "first_name"
            }, {
                label: "Last name:",
                name: "last_name"
            }, {
                label: "Position:",
                name: "position"
            }
        ]
    } );
	
	$('#employeesTable thead th').each( function () {
        var title = $(this).text();
        $(this).html( '<input type="text" placeholder="Search '+title+'" />' );
    } );
	var table = $('#employeesTable').DataTable({
		ajax: {
            "url": "/data/users",
            "type": "post"
        },
		serverSide : true,
		columns : [ {
			data : 'id'
		}, {
			data : 'firstName'
		}, {
			data : 'lastName'
		}, {
			data : 'email'
		} ],
		select: true,
        buttons: [
            { extend: "create", editor: editor },
            { extend: "edit",   editor: editor },
            { extend: "remove", editor: editor }
        ]
	});
	
	// Apply the search
    table.columns().every( function () {
        var that = this;
 
        $( 'input', this.header() ).on( 'keyup change', function () {
            if ( that.search() !== this.value ) {
                that
                    .search( this.value )
                    .draw();
            }
        } );
    } );
});