$(function() {

	$(document).ready(function () {
		console.log(window.location.href);
		var components = URI.parse(window.location.href);
		console.log(components);
		var query = URI.parseQuery(components['query']);
        if(query['errorSetTopBoxes']) {
        	$("#errorDiv").text('Error! '+query['savedElements']+'/'+query['totalElements'] + ' Saved. Set Top Boxes which are not Saved(Already Exists) : '+query['errorSetTopBoxes']);
        	$("#errorDiv").show();
        	$("#successDiv").hide();
        } else if(query['message']) {
        	$("#successDiv").text('Success! '+query['message']);
        	$("#errorDiv").hide();
        	$("#successDiv").show();
        } else {
        	$("#errorDiv").hide();
        	$("#successDiv").hide();
        }
    });
	
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
				sortname: 'setTopBoxNumber',
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
			width: 400,
		onclickSubmit: function(params, postdata) {
			params.url = 'setTopBox/' + postdata.id;
		}
	};
	var addOptions = {
			width: 400,
		onclickSubmit: function(params, postdata) {
			params.url = 'setTopBox';
		},
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'setTopBox/' + postdata;
		}
	};

	var URL = '/allSetTopBoxes';
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
				name:'setTopBoxNumber',
				label: 'Set Top Box Number',
				index: 'setTopBoxNumber',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'cardNumber',
				label: 'Card Number',
				index: 'cardNumber',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'safeCode',
				label: 'Safe Code',
				index: 'safeCode',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'setTopBoxStatus',
				label: 'Status',
				index: 'setTopBoxStatus',
				editable: true,
				edittype:"select",
		        editoptions:{ value: 'FREE:FREE;FAULTY:FAULTY;BLOCK:BLOCK;ALLOTED:ALLOTED;ACTIVATE:ACTIVATE;DE_ACTIVATE:DE ACTIVATE' },
				editrules: {required: true}
			}
		],
		caption: "Set Top Boxes",
		pager : '#pagerSetTopBoxes',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#setTopBoxes")
			.jqGrid(options)
			.navGrid('#pagerSetTopBoxes',
			{addtext: 'Add', edittext: 'Edit',deltext: 'Delete'}, //options
			editOptions,
			addOptions,
			delOptions,
			{} // search options
	);

	$("#setTopBoxes").jqGrid('filterToolbar', { stringResult: true, searchOnEnter: false });

});
