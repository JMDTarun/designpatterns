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
				sortname: 'setTopBoxNumber',
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
				closeAfterAdd: true,
				ajaxEditOptions: { contentType: "application/x-www-form-urlencoded" },
				mtype: 'POST',
				serializeEditData: function(data) {
					var url = Object.keys(data).map(function(k) {
					    return encodeURIComponent(k) + '=' + encodeURIComponent(data[k])
					}).join('&');
					return url;
				}
			});
	$.extend($.jgrid.del, {
				mtype: 'DELETE',
				serializeDelData: function() {
					return "";
				}
			});

	var editOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'setTopBox/' + postdata.id;
		}
	};
	var addOptions = {
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
				editoptions: {disabled: true, size:5}
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
			{}, //options
			editOptions,
			addOptions,
			delOptions,
			{} // search options
	);

	$("#setTopBoxes").jqGrid('filterToolbar', { stringResult: true, searchOnEnter: false });

});
