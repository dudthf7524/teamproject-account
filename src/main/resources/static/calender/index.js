let date = new Date();

const renderCalendar = () => {
    const viewYear = date.getFullYear();
    const viewMonth = date.getMonth();

    document.querySelector('.year-month').textContent = `${viewYear}년 ${viewMonth + 1}월`;

    const prevLast = new Date(viewYear, viewMonth, 0);
    const thisLast = new Date(viewYear, viewMonth + 1, 0);

    const PLDate = prevLast.getDate();
    const PLDay = prevLast.getDay();

    const TLDate = thisLast.getDate();
    const TLDay = thisLast.getDay();

    const prevDates = [];
    const thisDates = [...Array(TLDate + 1).keys()].slice(1);
    const nextDates = [];

    if (PLDay !== 6) {
        for (let i = 0; i < PLDay + 1; i++) {
            prevDates.unshift(PLDate - i);
        }
    }

    for (let i = 1; i < 7 - TLDay; i++) {
        nextDates.push(i);
    }

    const dates = prevDates.concat(thisDates, nextDates);
    const firstDateIndex = dates.indexOf(1);
    const lastDateIndex = dates.lastIndexOf(TLDate);
    const today = new Date();
    const isToday = (d, m, y) => d === today.getDate() && m === today.getMonth() && y === today.getFullYear();

    dates.forEach((date, i) => {
        const condition = i >= firstDateIndex && i < lastDateIndex + 1
            ? 'this'
            : 'other';
        const todayClass = isToday(date, viewMonth, viewYear) ? 'today' : '';
        dates[i] = `<div class="date ${condition} ${todayClass}" onclick="openModal(${viewYear}, ${viewMonth + 1}, ${date})"><span class="day-number">${date}</span></div>`;
    })

    document.querySelector('.dates').innerHTML = dates.join('');
};

const openModal = (year, month, day) => {
    const modal = document.getElementById("myModal");
    const modalBody = document.getElementById("modal-body");

    // 날짜 포맷팅
    const formattedMonth = month.toString().padStart(2, '0');
    const formattedDay = day.toString().padStart(2, '0');
    const formattedDate = `${year}-${formattedMonth}-${formattedDay}`;

    // 모달 내용 설정
    modalBody.innerHTML = `<p>선택한 날짜는 ${formattedDate} 입니다.</p>`;

    // 모달 창 띄우기
    modal.style.display = "block";
};

// 모달 창 닫기 기능
const closeModal = () => {
    const modal = document.getElementById("myModal");
    modal.style.display = "none";
};

// 모달 닫기 버튼 이벤트 연결
document.querySelector(".close").onclick = closeModal;

// 모달 영역 외부 클릭 시 모달 닫기
window.onclick = function(event) {
    const modal = document.getElementById("myModal");
    if (event.target === modal) {
        modal.style.display = "none";
    }
};

const prevMonth = () => {
    date.setMonth(date.getMonth() - 1);
    renderCalendar();
};

const nextMonth = () => {
    date.setMonth(date.getMonth() + 1);
    renderCalendar();
};

const goToday = () => {
    date = new Date();
    renderCalendar();
};

renderCalendar();