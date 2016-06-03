// Listen for messages
chrome.runtime.onMessage.addListener(function (msg, sender, sendResponse) {
    // If the received message has the expected format...
    if (msg === 'generate') {
        var nextButton = document.querySelectorAll("button[label='Next page']")[0];

        if (nextButton) {
            processPage();
            nextButton.click();
        } else {
            sendResponse('done');
            var printButton = document.querySelectorAll("input[label='Create expenses overview']")[0];
            printButton.click();
        }
    }
});

function isWeekend(transaction) {
    return transaction.children[0].innerHTML.indexOf("Saturday") >= 0
        || transaction.children[0].innerHTML.indexOf("Sunday") >= 0;
}
function isExpensive(transaction, upper) {
    return transaction.children[2].innerHTML.trim().substring(2) > upper;
}

function isLate(transaction, upper) {
    var rg = /(\d{1,2}:\d{2})/g;
    var transactionTime = rg.exec(transaction.children[1].innerHTML.trim())[1];
    return Date.parse('01/01/2011 ' + transactionTime) > Date.parse('01/01/2011 ' + upper);
}

function processPage() {
    var elements =  document.querySelectorAll("tr.known-transaction");
    for ( var i = 0; i < elements.length; i++ ) {
        var transaction = elements[i];
        if (isWeekend(transaction) || isExpensive(transaction, 5) || isLate(transaction, '20:00')) {
            // Uncheck
            var checkBox = transaction.children[3].children[0];
            if (checkBox.checked) {
                checkBox.click();
            }
        }
    }
}