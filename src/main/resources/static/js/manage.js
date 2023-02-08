const INTERVAL = 7000

function load() {
    setupTable()
    setInterval(() => {
        setupTable()
    }, INTERVAL)
}

function onMDMButtonClick(obj) {
    let uuid = obj.parentElement.children[1].innerHTML
    $.ajax({
        url: "../mdm/server/request",
        async: true,
        type: "post",
        contentType: "application/json",
        data: JSON.stringify({ data : uuid}),
        success: () => {
            alert("정상적으로 처리되었습니다.")
        },
        error: (error) => {
            alert("처리중 오류가 발생했습니다. 로그를 참조하세요.")
            console.log(error)
        }
    })
}

function onDisconnectButtonClick(obj) {
    let uuid = obj.parentElement.children[1].innerHTML
    $.ajax({
        url: "../mdm/server/disconnect",
        async: true,
        type: "post",
        contentType: "application/json",
        data: JSON.stringify({ data : uuid}),
        success: () => {
            alert("정상적으로 처리되었습니다.")
        },
        error: (error) => {
            alert("처리중 오류가 발생했습니다. 로그를 참조하세요.")
            console.log(error)
        }
    })
}

function setupTable() {
    $.ajax({
        type : 'get',
        url : '../mdm/status',
        async : true,
        dataType : 'text',       // 데이터 타입 (html, xml, json, text 등등)
        success : function(result) { // 결과 성공 콜백함수
            let body = $("#tbody") //body 비우기
            body.empty()
            let parse = JSON.parse(result)
            for (let i = 0; i < parse.data.length; i++) {
                let data = parse.data[i]
                let tr = ""
                tr += "<tr>"
                tr += "<td>" + data.id + "</td>"
                tr += "<td>" + data.uuid + "</td>"
                tr += "<td>" + data.delete + "</td>"
                tr += "<td>" + data.auth + "</td>"
                tr += "<td>" + data.mdmenabled + "<button onclick='onMDMButtonClick(this)'>▷</button><p style='visibility: hidden; width: 1px; height: 1px'>" + data.uuid + "</p></td>"
                if (data.serverConnected)
                    tr += "<td>" + data.serverConnected + "<button onclick='onDisconnectButtonClick(this)'>X</button><p style='visibility: hidden; width: 1px; height: 1px'>" + data.uuid + "</p></td>" //연결된경우 끊는 버튼
                else
                    tr += "<td>" + data.serverConnected  + "</td>"
                tr += "</tr>"
                body.append(tr)

            }
        },
        error : function(request, status, error) { // 결과 에러 콜백함수
            console.log(error)
        }
    })
}