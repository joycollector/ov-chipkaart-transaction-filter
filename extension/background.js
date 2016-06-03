var processingTabId = undefined;

function processTab(tab) {
    chrome.tabs.sendMessage(tab.id, 'generate', function (done) {
        if (done === 'done') {
            processingTabId = undefined;
        }
    });
}
chrome.browserAction.onClicked.addListener(function (tab) {
    processingTabId = tab.id;
    processTab(tab);
});

chrome.tabs.onUpdated.addListener(function (tabId, changeInfo, tab){
    if (processingTabId === tabId) {
        processTab(tab);
    }
});