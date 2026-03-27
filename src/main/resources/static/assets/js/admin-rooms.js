(() => {
    const root = document.body;
    const paginationEl = document.querySelector("[data-pagination]");
    const addRoomModalEl = document.getElementById("addRoomModal");
    const editRoomModalEl = document.getElementById("editRoomModal");
    const editRoomForm = document.getElementById("editRoomForm");

    function toNumber(value, fallback) {
        const parsed = Number(value);
        return Number.isFinite(parsed) ? parsed : fallback;
    }

    function buildPageUrl(baseUrl, page, pageSize, floor, active) {
        const params = new URLSearchParams();
        params.set("page", String(page));
        params.set("size", String(pageSize));
        if (floor) {
            params.set("floor", floor);
        }
        if (active) {
            params.set("active", active);
        }
        return `${baseUrl}?${params.toString()}`;
    }

    function createPageLink(label, targetPage, disabled, isActivePage, baseUrl, pageSize, floor, active) {
        const li = document.createElement("li");
        li.className = "page-item";
        if (disabled) li.classList.add("disabled");
        if (isActivePage) li.classList.add("active");

        const a = document.createElement("a");
        a.className = "page-link";
        a.textContent = label;
        if (disabled) {
            a.href = "#";
            a.tabIndex = -1;
            a.setAttribute("aria-disabled", "true");
        } else {
            a.href = buildPageUrl(baseUrl, targetPage, pageSize, floor, active);
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
        const active = paginationEl.dataset.active || "";
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

        navWrapper.appendChild(createPageLink("<<", 1, normalizedPage === 1, false, baseUrl, pageSize, floor, active));
        navWrapper.appendChild(createPageLink("이전", Math.max(1, normalizedPage - 1), normalizedPage === 1, false, baseUrl, pageSize, floor, active));

        for (let page = blockStart; page <= blockEnd; page += 1) {
            navWrapper.appendChild(createPageLink(String(page), page, false, page === normalizedPage, baseUrl, pageSize, floor, active));
        }

        navWrapper.appendChild(createPageLink("다음", Math.min(totalPages, normalizedPage + 1), normalizedPage === totalPages, false, baseUrl, pageSize, floor, active));
        navWrapper.appendChild(createPageLink(">>", totalPages, normalizedPage === totalPages, false, baseUrl, pageSize, floor, active));

        paginationEl.replaceChildren(navWrapper);
    }

    function bindFilters() {
        document.querySelectorAll(".floor-filter-select").forEach((select) => {
            select.addEventListener("change", () => {
                select.form?.requestSubmit();
            });
        });
    }

    function setFieldFeedback(input, feedback, type, message) {
        if (!input || !feedback) return;

        input.classList.remove("is-valid", "is-invalid");
        feedback.classList.remove("is-valid", "is-invalid");
        feedback.textContent = message || "";

        if (!type) {
            return;
        }

        input.classList.add(type);
        feedback.classList.add(type);
    }

    function createModalValidation(config) {
        const state = {
            floorValid: false,
            nameValid: false,
            capacityValid: false,
            descriptionValid: false,
            floorNameAvailable: false,
            floorNameCheckedValue: null
        };

        function currentFloorNameKey() {
            return `${config.floorInput.value.trim()}::${config.nameInput.value.trim()}`;
        }

        function clearFloorNameCheck() {
            state.floorNameAvailable = false;
            state.floorNameCheckedValue = null;
        }

        function updateSubmitState() {
            const canSubmit = state.floorValid
                && state.nameValid
                && state.capacityValid
                && state.descriptionValid
                && state.floorNameAvailable;
            config.submitButton.disabled = !canSubmit;
        }

        function validateFloor() {
            const value = Number(config.floorInput.value);
            if (!Number.isInteger(value) || value < 1 || value > 10) {
                state.floorValid = false;
                clearFloorNameCheck();
                setFieldFeedback(config.floorInput, config.floorFeedback, "is-invalid", "층수는 1층부터 10층까지만 입력할 수 있습니다.");
                updateSubmitState();
                return false;
            }

            state.floorValid = true;
            setFieldFeedback(config.floorInput, config.floorFeedback, "is-valid", "사용 가능한 층수입니다.");
            updateSubmitState();
            return true;
        }

        function validateName() {
            const value = config.nameInput.value.trim();
            if (!value) {
                state.nameValid = false;
                clearFloorNameCheck();
                setFieldFeedback(config.nameInput, config.nameFeedback, "is-invalid", "회의실 이름을 입력해 주세요.");
                updateSubmitState();
                return false;
            }
            if (value.length > 50) {
                state.nameValid = false;
                clearFloorNameCheck();
                setFieldFeedback(config.nameInput, config.nameFeedback, "is-invalid", "회의실 이름은 50자 이하여야 합니다.");
                updateSubmitState();
                return false;
            }

            state.nameValid = true;
            updateSubmitState();
            return true;
        }

        function validateCapacity() {
            const value = Number(config.capacityInput.value);
            if (!Number.isInteger(value) || value < 1 || value > 200) {
                state.capacityValid = false;
                setFieldFeedback(config.capacityInput, config.capacityFeedback, "is-invalid", "수용 인원은 1명부터 200명까지만 입력할 수 있습니다.");
                updateSubmitState();
                return false;
            }

            state.capacityValid = true;
            setFieldFeedback(config.capacityInput, config.capacityFeedback, "is-valid", "유효한 수용 인원입니다.");
            updateSubmitState();
            return true;
        }

        function validateDescription() {
            const value = config.descriptionInput.value.trim();
            if (!value) {
                state.descriptionValid = false;
                setFieldFeedback(config.descriptionInput, config.descriptionFeedback, "is-invalid", "설명을 입력해 주세요.");
                updateSubmitState();
                return false;
            }
            if (value.length > 100) {
                state.descriptionValid = false;
                setFieldFeedback(config.descriptionInput, config.descriptionFeedback, "is-invalid", "설명은 100자 이하여야 합니다.");
                updateSubmitState();
                return false;
            }

            state.descriptionValid = true;
            setFieldFeedback(config.descriptionInput, config.descriptionFeedback, "is-valid", "유효한 설명입니다.");
            updateSubmitState();
            return true;
        }

        async function checkFloorName() {
            if (!validateFloor() || !validateName()) {
                return false;
            }

            const currentKey = currentFloorNameKey();
            if (config.skipAvailabilityCheck && config.skipAvailabilityCheck()) {
                state.floorNameAvailable = true;
                state.floorNameCheckedValue = currentKey;
                setFieldFeedback(config.nameInput, config.nameFeedback, "is-valid", "현재 회의실 이름을 그대로 사용할 수 있습니다.");
                updateSubmitState();
                return true;
            }

            try {
                const params = new URLSearchParams({
                    floor: config.floorInput.value.trim(),
                    name: config.nameInput.value.trim()
                });
                const response = await fetch(`/admin/rooms/check-floor-name?${params.toString()}`, {
                    headers: {
                        "X-Requested-With": "XMLHttpRequest"
                    }
                });
                const payload = await response.json().catch(() => null);

                if (!response.ok || !payload?.success || !payload?.data) {
                    clearFloorNameCheck();
                    setFieldFeedback(config.nameInput, config.nameFeedback, "is-invalid", payload?.message || "회의실 중복 확인에 실패했습니다.");
                    updateSubmitState();
                    return false;
                }

                if (!payload.data.available) {
                    clearFloorNameCheck();
                    setFieldFeedback(config.nameInput, config.nameFeedback, "is-invalid", "선택한 층에 이미 존재하는 회의실입니다.");
                    updateSubmitState();
                    return false;
                }

                state.floorNameAvailable = true;
                state.floorNameCheckedValue = currentKey;
                setFieldFeedback(config.nameInput, config.nameFeedback, "is-valid", "선택한 층에서 사용 가능한 회의실 이름입니다.");
                updateSubmitState();
                return true;
            } catch (_error) {
                clearFloorNameCheck();
                setFieldFeedback(config.nameInput, config.nameFeedback, "is-invalid", "회의실 중복 확인에 실패했습니다.");
                updateSubmitState();
                return false;
            }
        }

        function invalidateNameAvailabilityFeedback() {
            clearFloorNameCheck();
            if (state.nameValid) {
                setFieldFeedback(config.nameInput, config.nameFeedback, null, "");
            }
            updateSubmitState();
        }

        function bind() {
            config.floorInput.addEventListener("input", invalidateNameAvailabilityFeedback);
            config.nameInput.addEventListener("input", invalidateNameAvailabilityFeedback);

            config.floorInput.addEventListener("blur", () => {
                validateFloor();
                if (state.nameValid) {
                    void checkFloorName();
                }
            });
            config.nameInput.addEventListener("blur", () => {
                validateName();
                if (state.floorValid) {
                    void checkFloorName();
                }
            });
            config.capacityInput.addEventListener("blur", validateCapacity);
            config.descriptionInput.addEventListener("blur", validateDescription);

            config.form.addEventListener("submit", async (event) => {
                const localValid = validateFloor() & validateName() & validateCapacity() & validateDescription();
                if (!localValid) {
                    event.preventDefault();
                    return;
                }

                const currentKey = currentFloorNameKey();
                if (!state.floorNameAvailable || state.floorNameCheckedValue !== currentKey) {
                    const available = await checkFloorName();
                    if (!available) {
                        event.preventDefault();
                    }
                }
            });
        }

        function resetForCurrentValues() {
            clearFloorNameCheck();
            setFieldFeedback(config.nameInput, config.nameFeedback, null, "");
            setFieldFeedback(config.floorInput, config.floorFeedback, null, "");
            setFieldFeedback(config.capacityInput, config.capacityFeedback, null, "");
            setFieldFeedback(config.descriptionInput, config.descriptionFeedback, null, "");
            state.floorValid = false;
            state.nameValid = false;
            state.capacityValid = false;
            state.descriptionValid = false;
            updateSubmitState();
        }

        bind();

        return {
            resetForCurrentValues,
            validateAll() {
                validateFloor();
                validateName();
                validateCapacity();
                validateDescription();
                if (state.floorValid && state.nameValid) {
                    void checkFloorName();
                }
            }
        };
    }

    function bindEditButtons(editValidation) {
        if (!editRoomForm) return;

        document.querySelectorAll(".edit-room-btn").forEach((button) => {
            button.addEventListener("click", () => {
                editRoomForm.action = `/admin/rooms/${button.dataset.roomId}`;
                document.getElementById("editName").value = button.dataset.roomName || "";
                document.getElementById("editFloor").value = button.dataset.roomFloor || 1;
                document.getElementById("editCapacity").value = button.dataset.roomCapacity || 1;
                document.getElementById("editDescription").value = button.dataset.roomDescription || "";
                editRoomForm.dataset.originalName = button.dataset.roomName || "";
                editRoomForm.dataset.originalFloor = button.dataset.roomFloor || "";
                editValidation.resetForCurrentValues();
                editValidation.validateAll();
            });
        });
    }

    function bindFilters() {
        document.querySelectorAll(".floor-filter-select").forEach((select) => {
            select.addEventListener("change", () => {
                select.form?.requestSubmit();
            });
        });
    }

    function openModalWhenNeeded() {
        const openAddModal = root.dataset.openAddModal === "true";
        const openEditModal = root.dataset.openEditModal === "true";

        if (openAddModal && addRoomModalEl) {
            bootstrap.Modal.getOrCreateInstance(addRoomModalEl).show();
        }

        if (openEditModal && editRoomModalEl) {
            bootstrap.Modal.getOrCreateInstance(editRoomModalEl).show();
        }
    }

    renderPagination();
    bindFilters();

    const addValidation = createModalValidation({
        form: document.getElementById("addRoomForm"),
        floorInput: document.getElementById("addFloor"),
        nameInput: document.getElementById("addName"),
        capacityInput: document.getElementById("addCapacity"),
        descriptionInput: document.getElementById("addDescription"),
        floorFeedback: document.getElementById("addFloorFeedback"),
        nameFeedback: document.getElementById("addNameFeedback"),
        capacityFeedback: document.getElementById("addCapacityFeedback"),
        descriptionFeedback: document.getElementById("addDescriptionFeedback"),
        submitButton: document.getElementById("addRoomSubmitButton")
    });

    const editValidation = createModalValidation({
        form: editRoomForm,
        floorInput: document.getElementById("editFloor"),
        nameInput: document.getElementById("editName"),
        capacityInput: document.getElementById("editCapacity"),
        descriptionInput: document.getElementById("editDescription"),
        floorFeedback: document.getElementById("editFloorFeedback"),
        nameFeedback: document.getElementById("editNameFeedback"),
        capacityFeedback: document.getElementById("editCapacityFeedback"),
        descriptionFeedback: document.getElementById("editDescriptionFeedback"),
        submitButton: document.getElementById("editRoomSubmitButton"),
        skipAvailabilityCheck() {
            const originalName = editRoomForm?.dataset.originalName?.trim() || "";
            const originalFloor = editRoomForm?.dataset.originalFloor?.trim() || "";
            return originalName === document.getElementById("editName").value.trim()
                && originalFloor === document.getElementById("editFloor").value.trim();
        }
    });

    bindEditButtons(editValidation);
    addValidation.resetForCurrentValues();
    editValidation.resetForCurrentValues();
    openModalWhenNeeded();

    if (root.dataset.openAddModal === "true") {
        addValidation.validateAll();
    }

    if (root.dataset.openEditModal === "true") {
        editValidation.validateAll();
    }
})();
