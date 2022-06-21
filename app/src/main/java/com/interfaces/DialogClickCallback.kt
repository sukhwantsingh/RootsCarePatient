package com.interfaces

interface DialogClickCallback {
    fun onConfirm(){}
    fun onDismiss(){}
}

interface DropDownDialogCallBack {
    fun onConfirm(text: String)

}

interface DropdownRowItemItemClickOnConfirm {
    fun onConfirm(title_name: String,title_id:String)
}

interface OnDropDownListItemClickListener {
    fun onConfirm(text: String)
}

interface OnRowItemDropdownItemClick {
    fun onConfirm(title_name: String, title_id: String)
}