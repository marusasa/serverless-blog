class AnalyticUtil{
	
	static currentPath:string = "";
	static visitorId: number = 0;
	static timeOnPage: number = 0;
	static INTERVAL: number = 1000;
	static hiddenVisibleSent: boolean = false;
	static lastSentStatus: string = "";
	
	static init(visitorId:number){
		this.visitorId = visitorId;
		setInterval(function()
		{
		    if (AnalyticUtil.currentPath != window.location.pathname)
		    {
				//URL path changed.
				//first send hidden to the previous path.
				if(AnalyticUtil.currentPath !== ""){
					AnalyticUtil.sendEvent(visitorId,AnalyticUtil.currentPath,'hidden');
				}
				AnalyticUtil.currentPath = window.location.pathname;
				AnalyticUtil.sendEvent(visitorId,window.location.pathname,document.visibilityState);	
				//reset some per-page
				AnalyticUtil.hiddenVisibleSent = false;
				AnalyticUtil.timeOnPage = 0;
		    }else{
				if(AnalyticUtil.hiddenVisibleSent === false && 
					AnalyticUtil.lastSentStatus == 'visible' &&
					document.visibilityState === 'visible'){
					//same page, page visible
					AnalyticUtil.timeOnPage = AnalyticUtil.timeOnPage + AnalyticUtil.INTERVAL;
					if(AnalyticUtil.timeOnPage >= 10000){
						//if more than 10 sec, then send
						//pair of hidden, vissible event.
						//This is to make sure a record is 
						//created for a page count, just in case
						//hidden is never sent to the server.
						AnalyticUtil.sendEvent(visitorId,AnalyticUtil.currentPath,'hidden&visible');
						AnalyticUtil.hiddenVisibleSent = true;
					}
				}
			}
			
		}, AnalyticUtil.INTERVAL);
		document.onvisibilitychange = () => {
			AnalyticUtil.sendEvent(visitorId,AnalyticUtil.currentPath,document.visibilityState);								
		};
	}
	
	static sendEvent(visitorId: number, path: string, state:string){
		console.log(new Date() + ' ' + visitorId + " " + path + " " + state);
		fetch("/analytics",{
			method: 'POST',
			headers: {
	            'Content-Type': 'application/json',
	        },
			body: JSON.stringify({
				visitorId: visitorId, path: path, state: state
			}),
			keepalive: true,
		}).catch((error) => {
			console.error(error);
		});
		AnalyticUtil.lastSentStatus = state;
	}	
}

export default AnalyticUtil;