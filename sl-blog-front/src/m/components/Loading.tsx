

function Loading({loaded}:{loaded:boolean}) {
		
	return (
		<div className={"flex justify-center mt-20 " + (loaded ? 'hidden' : 'visible')}>
			<div className="loading loading-bars loading-lg text-primary"></div>
		</div>
	)
}

export default Loading