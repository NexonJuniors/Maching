// tooltip.js
/**
 * 툴팁을 생성하는 함수
 * @param {string} content - 툴팁에 표시될 내용
 * @param {string} [tooltipClass='tooltip'] - 툴팁에 사용할 클래스 이름 (기본값: 'tooltip')
 * @returns {HTMLElement} - 생성된 툴팁 요소
 */
// 툴팁 생성 함수
function createTooltip(content) {
    const tooltip = document.createElement('div');
    tooltip.className = 'tooltip';
    tooltip.innerHTML = content;
    return tooltip;
}

/**
 * 이미지 요소와 툴팁을 포함한 컨테이너를 생성하는 함수
 * @param {string} imgSrc - 이미지 경로
 * @param {string} imgAlt - 이미지 대체 텍스트
 * @param {string} tooltipContent - 툴팁에 표시될 내용
 * @param {string} [wrapperClass='image-container'] - 이미지와 툴팁을 감싸는 컨테이너의 클래스 이름 (기본값: 'image-container')
 * @param {string} [imgClass='img-fluid'] - 이미지에 적용될 클래스 이름 (기본값: 'img-fluid')
 * @param {string} [tooltipClass='tooltip'] - 툴팁에 적용될 클래스 이름 (기본값: 'tooltip')
 * @returns {HTMLElement} - 이미지와 툴팁을 포함한 컨테이너 요소
 */
 //이건 그냥 보스 아이콘 전용이 될듯.
function createImageWithTooltip(imgSrc, imgAlt, tooltipContent, wrapperClass = 'image-container', imgClass = 'img-fluid', tooltipClass = 'tooltip') {
    const imgContainer = document.createElement('div');
    imgContainer.className = wrapperClass;

    const img = document.createElement('img');
    img.src = imgSrc;
    img.alt = imgAlt;
    img.className = imgClass;

    const tooltip = createTooltip(tooltipContent, tooltipClass);

    imgContainer.appendChild(img);
    imgContainer.appendChild(tooltip);

    return imgContainer;
}