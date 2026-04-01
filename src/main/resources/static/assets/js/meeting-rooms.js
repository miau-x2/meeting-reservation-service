(() => {
    const paginationEl = document.querySelector("[data-pagination]");
    const reservationPanelEl = document.querySelector("[data-reservation-panel]");

    function toNumber(value, fallback) {
        const parsed = Number(value);
        return Number.isFinite(parsed) ? parsed : fallback;
    }

    function buildPageUrl(baseUrl, page, pageSize, floor) {
        const params = new URLSearchParams();
        if (floor) {
            params.set("floor", floor);
        }
        params.set("page", String(page));
        params.set("size", String(pageSize));
        return `${baseUrl}?${params.toString()}`;
    }

    function createPageLink(label, targetPage, disabled, active, baseUrl, pageSize, floor) {
        const li = document.createElement("li");
        li.className = "page-item";
        if (disabled) li.classList.add("disabled");
        if (active) li.classList.add("active");

        const a = document.createElement("a");
        a.className = "page-link";
        a.textContent = label;
        if (disabled) {
            a.href = "#";
            a.tabIndex = -1;
            a.setAttribute("aria-disabled", "true");
        } else {
            a.href = buildPageUrl(baseUrl, targetPage, pageSize, floor);
        }
        li.appendChild(a);
        return li;
    }

    function renderPagination() {
        if (!paginationEl) return;

        const currentPage = toNumber(paginationEl.dataset.currentPage, 1);
        const totalPages = toNumber(paginationEl.dataset.totalPages, 0);
        const pageSize = toNumber(paginationEl.dataset.pageSize, 10);
        const floor = paginationEl.dataset.floor || "";
        const baseUrl = paginationEl.dataset.baseUrl || "";

        if (totalPages <= 0) {
            paginationEl.innerHTML = "";
            return;
        }

        const blockSize = 10;
        const normalizedPage = Math.min(Math.max(currentPage, 1), totalPages);
        const blockStart = Math.floor((normalizedPage - 1) / blockSize) * blockSize + 1;
        const blockEnd = Math.min(blockStart + blockSize - 1, totalPages);

        const navWrapper = document.createElement("ul");
        navWrapper.className = "pagination justify-content-center flex-wrap gap-1";

        navWrapper.appendChild(createPageLink("<<", 1, normalizedPage === 1, false, baseUrl, pageSize, floor));
        navWrapper.appendChild(createPageLink("이전", Math.max(1, normalizedPage - 1), normalizedPage === 1, false, baseUrl, pageSize, floor));

        for (let page = blockStart; page <= blockEnd; page += 1) {
            navWrapper.appendChild(createPageLink(String(page), page, false, page === normalizedPage, baseUrl, pageSize, floor));
        }

        navWrapper.appendChild(createPageLink("다음", Math.min(totalPages, normalizedPage + 1), normalizedPage === totalPages, false, baseUrl, pageSize, floor));
        navWrapper.appendChild(createPageLink(">>", totalPages, normalizedPage === totalPages, false, baseUrl, pageSize, floor));

        paginationEl.replaceChildren(navWrapper);
    }

    function bindFilters() {
        document.querySelectorAll(".floor-filter-select").forEach((select) => {
            select.addEventListener("change", () => {
                select.form?.requestSubmit();
            });
        });
    }

    function formatIsoDate(date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, "0");
        const day = String(date.getDate()).padStart(2, "0");
        return `${year}-${month}-${day}`;
    }

    function startOfDay(date) {
        const copy = new Date(date);
        copy.setHours(0, 0, 0, 0);
        return copy;
    }

    function addDays(date, days) {
        const copy = new Date(date);
        copy.setDate(copy.getDate() + days);
        return startOfDay(copy);
    }

    function setDateRange(inputEl) {
        const today = startOfDay(new Date());
        const maxDate = addDays(today, 7);
        inputEl.min = formatIsoDate(today);
        inputEl.max = formatIsoDate(maxDate);
        inputEl.value = formatIsoDate(today);
    }

    function renderTimeSlotOptions(selectEl, items) {
        if (!items.length) {
            const option = document.createElement("option");
            option.value = "";
            option.textContent = "선택 가능한 시간이 없습니다";
            selectEl.replaceChildren(option);
            return;
        }

        selectEl.replaceChildren(
            ...items.map((item) => {
                const option = document.createElement("option");
                option.value = item.startTime;
                option.textContent = `${item.startTime} - ${item.endTime}`;
                return option;
            })
        );
    }

    function bindRoomSelection() {
        if (!reservationPanelEl) return;

        const placeholderEl = reservationPanelEl.querySelector("[data-reservation-placeholder]");
        const contentEl = reservationPanelEl.querySelector("[data-reservation-content]");
        const floorEl = reservationPanelEl.querySelector("[data-selected-room-floor]");
        const nameEl = reservationPanelEl.querySelector("[data-selected-room-name]");
        const descriptionEl = reservationPanelEl.querySelector("[data-selected-room-description]");
        const dateInputEl = reservationPanelEl.querySelector("[data-reservation-date]");
        const durationSelectEl = reservationPanelEl.querySelector("[data-duration-select]");
        const timeSlotSelectEl = reservationPanelEl.querySelector("[data-time-slot-select]");
        const attendeeInputEl = reservationPanelEl.querySelector("[data-attendee-input]");
        const attendeeHelpEl = reservationPanelEl.querySelector("[data-attendee-help]");
        const attendeeErrorEl = reservationPanelEl.querySelector("[data-attendee-error]");
        const reservationSubmitEl = reservationPanelEl.querySelector("[data-reservation-submit]");
        const reservationErrorEl = reservationPanelEl.querySelector("[data-reservation-error]");
        const reservationSuccessEl = reservationPanelEl.querySelector("[data-reservation-success]");
        const roomRows = document.querySelectorAll("[data-room-row]");

        if (
            !placeholderEl || !contentEl || !floorEl || !nameEl || !descriptionEl
            || !dateInputEl || !durationSelectEl || !timeSlotSelectEl
            || !attendeeInputEl || !attendeeHelpEl || !attendeeErrorEl
            || !reservationSubmitEl || !reservationErrorEl || !reservationSuccessEl
            || roomRows.length === 0
        ) {
            return;
        }

        let currentCapacity = 0;
        let currentRoomId = null;

        function clearReservationMessages() {
            reservationErrorEl.classList.add("d-none");
            reservationErrorEl.textContent = "";
            reservationSuccessEl.classList.add("d-none");
            reservationSuccessEl.textContent = "";
        }

        async function loadAvailableSlots() {
            if (!currentRoomId || !dateInputEl.value) {
                return;
            }

            const duration = durationSelectEl.value;
            const query = new URLSearchParams({
                date: dateInputEl.value,
                duration
            });

            try {
                const response = await fetch(`/rooms/${currentRoomId}/slots?${query.toString()}`);
                if (!response.ok) {
                    throw new Error("failed");
                }

                const payload = await response.json();
                const items = payload.data ?? [];
                renderTimeSlotOptions(timeSlotSelectEl, items);
            } catch (error) {
                renderTimeSlotOptions(timeSlotSelectEl, []);
            }
        }

        function validateAttendeeCount() {
            const value = Number(attendeeInputEl.value);

            if (!attendeeInputEl.value) {
                attendeeInputEl.classList.remove("is-invalid");
                attendeeErrorEl.classList.add("d-none");
                attendeeErrorEl.textContent = "";
                return true;
            }

            if (value < 1 || value > currentCapacity) {
                attendeeInputEl.classList.add("is-invalid");
                attendeeErrorEl.textContent = `참석 인원은 1명 이상 ${currentCapacity}명 이하로 입력해야 합니다.`;
                attendeeErrorEl.classList.remove("d-none");
                attendeeInputEl.value = currentCapacity > 0 ? String(currentCapacity) : "";
                return false;
            }

            attendeeInputEl.classList.remove("is-invalid");
            attendeeErrorEl.classList.add("d-none");
            attendeeErrorEl.textContent = "";
            return true;
        }

        async function submitReservation() {
            clearReservationMessages();

            if (!currentRoomId) {
                reservationErrorEl.textContent = "회의실을 먼저 선택해주세요.";
                reservationErrorEl.classList.remove("d-none");
                return;
            }

            if (!dateInputEl.value || !timeSlotSelectEl.value || !attendeeInputEl.value) {
                reservationErrorEl.textContent = "예약 날짜, 예약 시간, 참석 인원을 모두 입력해주세요.";
                reservationErrorEl.classList.remove("d-none");
                return;
            }

            if (!validateAttendeeCount()) {
                return;
            }

            const payload = {
                date: dateInputEl.value,
                startTime: timeSlotSelectEl.value,
                durationHours: Number(durationSelectEl.value),
                attendeeCount: Number(attendeeInputEl.value)
            };

            reservationSubmitEl.disabled = true;

            try {
                const response = await fetch(`/rooms/${currentRoomId}/reservations`, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify(payload)
                });

                const result = await response.json().catch(() => null);
                if (!response.ok) {
                    throw new Error(result?.message || "예약에 실패했습니다.");
                }

                reservationSuccessEl.textContent = "예약이 완료되었습니다.";
                reservationSuccessEl.classList.remove("d-none");
                await loadAvailableSlots();
            } catch (error) {
                reservationErrorEl.textContent = error.message || "예약에 실패했습니다.";
                reservationErrorEl.classList.remove("d-none");
            } finally {
                reservationSubmitEl.disabled = false;
            }
        }

        durationSelectEl.addEventListener("change", () => {
            clearReservationMessages();
            loadAvailableSlots();
        });

        dateInputEl.addEventListener("change", () => {
            clearReservationMessages();
            loadAvailableSlots();
        });

        attendeeInputEl.addEventListener("blur", validateAttendeeCount);
        reservationSubmitEl.addEventListener("click", submitReservation);

        roomRows.forEach((row) => {
            row.addEventListener("click", () => {
                roomRows.forEach((target) => target.classList.remove("room-row-active"));
                row.classList.add("room-row-active");

                currentRoomId = row.dataset.roomId;
                currentCapacity = Number(row.dataset.roomCapacity);
                floorEl.textContent = `${row.dataset.roomFloor}층`;
                nameEl.textContent = row.dataset.roomName || "";
                descriptionEl.textContent = row.dataset.roomDescription || "";

                setDateRange(dateInputEl);
                durationSelectEl.value = "1";
                attendeeInputEl.min = "1";
                attendeeInputEl.max = String(currentCapacity);
                attendeeInputEl.value = "";
                attendeeInputEl.placeholder = `최대 ${currentCapacity}명`;
                attendeeHelpEl.textContent = `정원 ${currentCapacity}명까지 입력할 수 있습니다.`;
                attendeeInputEl.classList.remove("is-invalid");
                attendeeErrorEl.classList.add("d-none");
                attendeeErrorEl.textContent = "";
                clearReservationMessages();

                placeholderEl.classList.add("d-none");
                contentEl.classList.remove("d-none");

                loadAvailableSlots();
            });
        });
    }

    renderPagination();
    bindFilters();
    bindRoomSelection();
})();
