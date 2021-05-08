var ClusterSetting = function () {
    this.gameServer = {
        protocol: 'ws',
        //host: 'localhost',
        host: 'dry-meadow-58892.herokuapp.com',
        port: '8090',
        path: '/events/connect'
    };

    this.matchMaker = {
        //protocol: 'http',
        protocol: 'https',
        //host: 'localhost',
        host: 'dry-meadow-58892.herokuapp.com',
        port: '8080',
        path: '/matchmaker/join'
    };
};

ClusterSetting.prototype.gameServerUrl = function() {
    return makeUrl(this.gameServer)
};

ClusterSetting.prototype.matchMakerUrl = function() {
    return makeUrl(this.matchMaker)
};

function makeUrl(data) {
    return data['protocol'] + "://" + data['host'] + ":" + data['port'] + data['path']
}

var gClusterSettings = new ClusterSetting();
