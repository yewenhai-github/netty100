import loadingUrl from './loading.svg'
import './loading.css'

function getLoadingImg(el) { // 查找imgDOM
	//查找图片中有data-role为loading的图片
	return el.querySelector('#netty-loading-mask');
}

function createLoadingMask(el) { // 创建mask
	const mask = document.createElement("div");
	const img = document.createElement("img");
	//添加data-role为loading的属性
	img.dataset.role = 'loading';
	img.src = loadingUrl;
	img.className = "loading";
	mask.className = "loading-mask";
	mask.id = "netty-loading-mask";
	mask.style.width = el.offsetWidth == 0 ? "100px" : el.offsetWidth + "px";
	mask.style.height = el.offsetHeight == 0 ? "100px" : el.offsetHeight + "px";
	mask.appendChild(img);
	return mask;
}


export default function (el, data) {
	const loadingDom = getLoadingImg(el);
	if (data.value) { // 显示
		if (!loadingDom) { // 不存在  创建  插入
			const mask = createLoadingMask(el);
			el.appendChild(mask)
		}
	} else { // 不显示
		if (loadingDom) { // 当前img标签存在则移除
			el.removeChild(loadingDom);
		}
	}
}
