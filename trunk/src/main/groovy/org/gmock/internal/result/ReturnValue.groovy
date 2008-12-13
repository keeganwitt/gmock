package org.gmock.internal.result

class ReturnValue {

    def value

    ReturnValue(value){
        this.value = value
    }

    def answer(){
        return value
    }

}
