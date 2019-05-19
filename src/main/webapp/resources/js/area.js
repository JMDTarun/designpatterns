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
				sortname: 'name',
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
					delete data.oper;
					return JSON.stringify(data);
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
			params.url = 'area/' + postdata.id;
		}
	};
	var addOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'area';
		},
		mtype: "POST"
	};
	var delOptions = {
		onclickSubmit: function(params, postdata) {
			params.url = 'area/' + postdata;
		}
	};

	var URL = '/allAreas';
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
				name:'name',
				label: 'Name',
				index: 'name',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'name2',
				label: 'Name 2',
				index: 'name2',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'areaCode',
				label: 'Area Code',
				index: 'areaCode',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'lcoCode',
				label: 'Lco Code',
				index: 'lcoCode',
				editable: true,
				editrules: {required: true}
			},
			{
				name:'lcoName',
				label: 'Lco Name',
				index: 'lcoName',
				editable: true,
				editrules: {required: true}
			}
		],
		caption: "Areas",
		pager : '#pagerAreas',
		height: 'auto',
		ondblClickRow: function(id) {
			jQuery(this).jqGrid('editGridRow', id, editOptions);
		}
	};

	$("#areas")
			.jqGrid(options)
			.navGrid('#pagerAreas',
			{}, //options
			editOptions,
			addOptions,
			delOptions,
			{} // search options
	);

	$("#areas").jqGrid('filterToolbar', { stringResult: true, searchOnEnter: false });

});
