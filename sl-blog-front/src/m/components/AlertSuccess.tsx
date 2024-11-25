import { useEffect } from 'react';

function AlertSuccess({message,showAlert,setShowAlert}:{message:text,showAlert:boolean,setShowAlert:(a: boolean) => void}) {
		
	useEffect(() => {
			if(showAlert){
				setTimeout(() => {
				  setShowAlert(false);
				}, "2000");
			}
		}, [showAlert]);
	
	return (
		<div role="alert" className={"fixed top-0 right-0 w-96 alert alert-success z-50 " + (showAlert?'':'hidden')}>
			<svg
				xmlns="http://www.w3.org/2000/svg"
				className="h-6 w-6 shrink-0 stroke-current"
				fill="none"
				viewBox="0 0 24 24">
				<path
					strokeLinecap="round"
					strokeLinejoin="round"
					strokeWidth="2"
					d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
			</svg>
			<span>{message}</span>
		</div>
	)
}

export default AlertSuccess