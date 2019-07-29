$(function() {

	$.extend($.jgrid.defaults, {
				datatype: 'json',
				jsonReader : {
					repeatitems:false,
					total: function(result) {
						//Total number of pages
						return Math.ceil(result.total / result.max);
					},
					records: function(result) {
						//Total number of records
						return result.total;
					}
				},
				prmNames: {
					page: "page",
					rows: "size",
					sort: "sort"
				},
				sortname: 'firstName',
				sortorder: 'asc',
				height: 'auto',
				viewrecords: true,
				rowList: [10, 20, 50, 100],
				altRows: true,
				loadError: function(xhr, status, error) {
					alert(error);
				}
			});

	$.extend($.jgrid.edit, {
				closeAfterEdit: true,
				closeAfterAdd: false,
				ajaxEditOptions: { contentType: "application/x-www-form-urlencoded" },
				mtype: 'POST',
				serializeEditData: function(data) {
					var url = Object.keys(data).map(function(k) {
						console.info(k+"!!!"+data[k]);
					    return encodeURIComponent(k) + '=' + encodeURIComponent(data[k].replace("_empty", ""))
					}).join('&');
					return url;
//					delete data.oper;
//					return JSON.stringify(data);
				},
				errorTextFormat: function (response) {
					if(response.responseText) {
						var obj = JSON.parse(response.responseText);
						if(obj.errorCode && obj.errorCode != null) {
							return obj.errorCode+" "+ obj.errorCause;
						}
					}
				    return "Data Saved!";
				}
			});
	$.extend($.jgrid.del, {
				mtype: 'DELETE',
				serializeDelData: function() {
					return "";
				}
			});

	var editOptions = {
			width: 700,
		onclickSubmit: function(params, postdata) {
			params.url = 'user/' + postdata.id;
		},
		mtype: "POST"
	};
	var addOptions = {
			width: 700,
		onclickSubmit: function(params, postdata) {
			params.url = 'user';
		},
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'user/' + postdata;
		}
	};

	var URL = '/allUsers';
	var options = {
		url: URL,
		editurl: URL,
		height: 'auto',
        forceFit: true,
        autowidth: true,
        shrinkToFit: true,
        rowNum: 10,
		colModel:[
			{
				name:'id',
				label: 'ID',
				index: 'id',
				formatter:'integer',
				editable: true,
				hidden: true, 
				editrules: { edithidden: false }
			},
			{
				name:'firstName',
				label: 'First Name',
				index: 'firstName',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'lastName',
				label: 'Last Name',
				index: 'lastName',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'email',
				label: 'Email',
				index: 'email',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'password',
				label: 'Password',
				index: 'password',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'roleName',
				label: 'Role',
				index: 'roleName',
				editable: true,
				edittype:"select",
		        editoptions:{ value: 'ADMIN:ADMIN;USER:USER' },
				editrules: {required: true}
			}
		],
		caption: "Users",
		pager : '#pagerUsers',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#users")
			.jqGrid(options)
			.navGrid('#pagerUsers',
			{addtext: 'Add', search:false, edittext: 'Edit',deltext: 'Delete'}, //options
			editOptions,
			addOptions,
			delOptions,
			{} // search options
	);

	$("#pagerUsers").css({"height":"55"});
	
	$("#users").jqGrid('filterToolbar', { stringResult: true, searchOnEnter: false });

});
