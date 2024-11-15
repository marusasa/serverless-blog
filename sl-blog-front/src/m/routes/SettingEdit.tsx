import { useState, useEffect } from 'react';
import Loading from "../components/Loading";
import {SubmitButton, FormTitle} from "../components/FormComp";

function SettingEdit() {

	const [blogTitle, setBlogTitle] = useState('');
	const [blogSubTitle, setBlogSubTitle] = useState('');
	const [iconUrl, setIconUrl] = useState('');
	const [faviconUrl, setFaviconUrl] = useState('');
	const [settingId, setSettingId] = useState('');
	const [loaded, setLoaded] = useState(false);
	const [inProcess,setInProcess] = useState(false);

	useEffect(() => {
		fetch('/mng/setting')
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					setBlogTitle(data.setting.blogTitle);
					setBlogSubTitle(data.setting.blogSubTitle);
					setSettingId(data.setting.settingId);
					setIconUrl(data.setting.iconUrl);
					setFaviconUrl(data.setting.faviconUrl);
					setLoaded(true);
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				console.log(err.message);
				alert('Failed to load setting.');
			});
	}, []);


	const updateSetting = async (title: string, subTitle: string) => {
		await fetch('/mng/setting', {
			method: 'PATCH',
			body: JSON.stringify({
				blogTitle: title,
				blogSubTitle: subTitle,
				iconUrl: iconUrl,
				faviconUrl: faviconUrl,
				settingId: settingId
				
			}),
			headers: {
				'Content-type': 'application/json; charset=UTF-8',
			},
		})
			.then((response) => response.json())
			.then((data) => {
				if (data.result == 'success') {
					alert('Data saved.');
				} else {
					alert(JSON.stringify(data.messages));
				}
			})
			.catch((err) => {
				alert('Failed to save setting.');
				console.log(err.message);
			})
			.finally(() => {
				setInProcess(false);
			});
	};

	const handleSave = (e: React.FormEvent) => {
		e.preventDefault();
		setInProcess(true);
		updateSetting(blogTitle, blogSubTitle);
	};

	return (
		<>
			<FormTitle text="Edit Setting"/>
			<Loading loaded={loaded} />
			<div className={loaded ? 'visible' : 'invisible'}>
				<div className="card bg-base-100 w-96 w-full mb-4 bg-secondary-content">
					<div className="card-body p-4">
						<h2 className="card-title">Note</h2>
						<p>When your system has "basic.use-hardcoded-val=true" in serverless-blog.properties file, 
							the values you enter here are ignored.</p>
					</div>
				</div>
				<form onSubmit={handleSave}>
					<div className="flex">
						<SubmitButton text="Save" inProcess={inProcess} callback={handleSave} classes="btn-sm btn-primary"/>
					</div>
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Blog Title:</span>
						</div>
						<input type="text" className="input input-bordered w-full max-w-5xl" value={blogTitle}
							onChange={(e) => setBlogTitle(e.target.value)} />
					</label>
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Blog Sub-Title:</span>
						</div>
						<input type="text" className="input input-bordered w-full max-w-5xl" value={blogSubTitle}
												onChange={(e) => setBlogSubTitle(e.target.value)} />
					</label>	
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Icon URL:</span>
						</div>
						<input type="text" className="input input-bordered w-full max-w-5xl" value={iconUrl}
												onChange={(e) => setIconUrl(e.target.value)} />
					</label>	
					<label className="form-control mb-4">
						<div className="label">
							<span className="label-text">Favicon URL:</span>
						</div>
						<input type="text" className="input input-bordered w-full max-w-5xl" value={faviconUrl}
												onChange={(e) => setFaviconUrl(e.target.value)} />
					</label>
				</form>
			</div>

		</>
	)
}

export default SettingEdit