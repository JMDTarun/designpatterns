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
				sortname: 'customerType',
				sortorder: 'asc',
				height: 'auto',
				forceFit: true,
		        autowidth: true,
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
					    return encodeURIComponent(k) + '=' + encodeURIComponent(data[k].replace("_empty", ""))
					}).join('&');
					return url;
					//delete data.oper;
					//return JSON.stringify(data);
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
			params.url = 'customerType/' + postdata.id;
		}
	};
	var addOptions = {
			width: 700,
		onclickSubmit: function(params, postdata) {
			params.url = 'customerType';
		},
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'customerType/' + postdata;
		}
	};

	var URL = '/allCustomerTypes';
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
				name:'customerType',
				label: 'Customer Type',
				index: 'customerType',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'maxAmount',
				label: 'Max Amount',
				index: 'maxAmount',
				editable: true,
				editrules: {required: true}
			}	
		],
		caption: "Customer Types",
		pager : '#pagerCustomerTypes',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#customerTypes")
			.jqGrid(options)
			.navGrid('#pagerCustomerTypes',
			{addtext: 'Add', edittext: 'Edit',deltext: 'Delete'}, //options
			editOptions,
			addOptions,
			delOptions,
			{} // search options
	);

	$("#pagerCustomerTypes").jqGrid('filterToolbar', { stringResult: true, searchOnEnter: false });

});
