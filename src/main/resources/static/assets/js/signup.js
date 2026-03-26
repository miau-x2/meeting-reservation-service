(() => {
    const $ = (id) => document.getElementById(id);

    const form = $("signupForm");
    if (!form) return;

    const signupBtn = $("signupBtn");
    const globalErrorJs = $("globalErrorJs");

    const usernameInput = $("username");
    const passwordInput = $("password");
    const nameInput = $("name");

    const usernameFeedback = $("usernameFeedback");
    const passwordFeedback = $("passwordFeedback");
    const nameFeedback = $("nameFeedback");

    const togglePasswordBtn = $("togglePasswordBtn");
    const togglePasswordIcon = $("togglePasswordIcon");

    const USERNAME_REGEX = /^(?=.*[a-z])[a-z0-9]+$/;
    const PASSWORD_REGEX = /^(?=\S+$)(?=.*[A-Za-z])(?=.*\d)(?=.*[!@#$%^&*()])[A-Za-z\d!@#$%^&*()]+$/;
    const NAME_REGEX = /^[가-힣]+$/;

    const state = {
        usernameValid: false,
        passwordValid: false,
        nameValid: false,
        lastUsernameChecked: null,
        usernameAvailable: false,
        usernameRequestSeq: 0,
        submitting: false,
    };

    function clearGlobalError() {
        if (!globalErrorJs) return;
        globalErrorJs.hidden = true;
        globalErrorJs.textContent = "";
    }

    function showGlobalError(message) {
        if (!globalErrorJs) return;
        globalErrorJs.textContent = message || "요청 처리 중 오류가 발생했습니다.";
        globalErrorJs.hidden = false;
    }

    function setFeedback(input, feedback, ok, message) {
        input.classList.toggle("is-valid", ok);
        input.classList.toggle("is-invalid", !ok);
        feedback.classList.toggle("is-valid", ok);
        feedback.classList.toggle("is-invalid", !ok);
        feedback.textContent = message || "";
    }

    function clearFeedback(input, feedback) {
        input.classList.remove("is-valid", "is-invalid");
        feedback.classList.remove("is-valid", "is-invalid");
        feedback.textContent = "";
    }

    async function safeFetchJson(url) {
        try {
            const res = await fetch(url, {
                method: "GET",
                credentials: "same-origin",
                headers: { Accept: "application/json" },
            });
            const text = await res.text();
            return { res, json: text ? JSON.parse(text) : null };
        } catch {
            return { res: null, json: null };
        }
    }

    function validateUsernameLocal() {
        clearGlobalError();

        const value = (usernameInput.value || "").trim();
        usernameInput.value = value;

        if (!value) {
            state.usernameValid = false;
            state.lastUsernameChecked = null;
            state.usernameAvailable = false;
            setFeedback(usernameInput, usernameFeedback, false, "아이디를 입력해주세요.");
            return false;
        }

        if (value.length < 5 || value.length > 20) {
            state.usernameValid = false;
            state.lastUsernameChecked = null;
            state.usernameAvailable = false;
            setFeedback(usernameInput, usernameFeedback, false, "아이디는 5~20자입니다.");
            return false;
        }

        if (!USERNAME_REGEX.test(value)) {
            state.usernameValid = false;
            state.lastUsernameChecked = null;
            state.usernameAvailable = false;
            setFeedback(usernameInput, usernameFeedback, false, "아이디는 영문 소문자와 숫자만 가능하며 영문은 필수입니다.");
            return false;
        }

        state.usernameValid = true;
        return true;
    }

    async function validateUsernameOnBlur() {
        const locallyValid = validateUsernameLocal();
        if (!locallyValid) {
            return false;
        }

        const value = usernameInput.value;
        if (state.lastUsernameChecked === value) {
            setFeedback(
                usernameInput,
                usernameFeedback,
                state.usernameAvailable,
                state.usernameAvailable ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다."
            );
            return state.usernameAvailable;
        }

        const seq = ++state.usernameRequestSeq;
        const { res, json } = await safeFetchJson(`/employees/check-username?username=${encodeURIComponent(value)}`);
        if (seq !== state.usernameRequestSeq) {
            return false;
        }

        if (!res || !res.ok || !json) {
            state.lastUsernameChecked = null;
            state.usernameAvailable = false;
            setFeedback(usernameInput, usernameFeedback, false, json?.message || "아이디 중복 확인에 실패했습니다.");
            return false;
        }

        state.lastUsernameChecked = value;
        state.usernameAvailable = !!json.data?.available;

        setFeedback(
            usernameInput,
            usernameFeedback,
            state.usernameAvailable,
            state.usernameAvailable ? "사용 가능한 아이디입니다." : "이미 사용 중인 아이디입니다."
        );

        return state.usernameAvailable;
    }

    function validatePassword() {
        clearGlobalError();

        const value = passwordInput.value || "";
        if (!value.trim()) {
            state.passwordValid = false;
            setFeedback(passwordInput, passwordFeedback, false, "비밀번호를 입력해주세요.");
            return false;
        }

        if (value.length < 8 || value.length > 20) {
            state.passwordValid = false;
            setFeedback(passwordInput, passwordFeedback, false, "비밀번호는 8~20자입니다.");
            return false;
        }

        if (!PASSWORD_REGEX.test(value)) {
            state.passwordValid = false;
            setFeedback(passwordInput, passwordFeedback, false, "비밀번호는 영문, 숫자, 특수문자('!', '@', '#', '$', '%', '^', '&', '*', '(', ')')를 각각 1개 이상 포함해야 하며 공백은 사용할 수 없습니다.");
            return false;
        }

        state.passwordValid = true;
        setFeedback(passwordInput, passwordFeedback, true, "사용 가능한 비밀번호입니다.");
        return true;
    }

    function validateName() {
        clearGlobalError();

        const value = (nameInput.value || "").trim();
        nameInput.value = value;

        if (!value) {
            state.nameValid = false;
            setFeedback(nameInput, nameFeedback, false, "이름을 입력해주세요.");
            return false;
        }

        if (value.length < 2 || value.length > 20) {
            state.nameValid = false;
            setFeedback(nameInput, nameFeedback, false, "이름은 2~20자입니다.");
            return false;
        }

        if (!NAME_REGEX.test(value)) {
            state.nameValid = false;
            setFeedback(nameInput, nameFeedback, false, "이름은 한글만 사용할 수 있습니다.");
            return false;
        }

        state.nameValid = true;
        setFeedback(nameInput, nameFeedback, true, "사용 가능한 이름입니다.");
        return true;
    }

    async function runSubmitValidationAndMaybeSubmit() {
        if (state.submitting) return;

        const passwordOk = validatePassword();
        const nameOk = validateName();

        let usernameOk;
        if (state.lastUsernameChecked === (usernameInput.value || "").trim() && state.usernameAvailable) {
            usernameOk = true;
        } else {
            usernameOk = await validateUsernameOnBlur();
        }

        if (!(usernameOk && passwordOk && nameOk)) {
            form.querySelector(".form-control.is-invalid")?.focus();
            return;
        }

        state.submitting = true;
        if (signupBtn) signupBtn.disabled = true;
        form.submit();
    }

    usernameInput.addEventListener("input", () => {
        state.lastUsernameChecked = null;
        state.usernameAvailable = false;
        state.usernameValid = false;
        clearFeedback(usernameInput, usernameFeedback);
    });
    usernameInput.addEventListener("blur", () => {
        validateUsernameOnBlur().catch(() => {
            state.lastUsernameChecked = null;
            state.usernameAvailable = false;
            setFeedback(usernameInput, usernameFeedback, false, "아이디 중복 확인에 실패했습니다.");
        });
    });
    passwordInput.addEventListener("blur", validatePassword);
    nameInput.addEventListener("blur", validateName);

    if (togglePasswordBtn) {
        togglePasswordBtn.addEventListener("click", () => {
            const isPassword = passwordInput.type === "password";
            passwordInput.type = isPassword ? "text" : "password";
            togglePasswordIcon.className = isPassword ? "bi bi-eye" : "bi bi-eye-slash";
            togglePasswordBtn.setAttribute("aria-pressed", String(isPassword));
        });
    }

    form.addEventListener("submit", (event) => {
        event.preventDefault();
        event.stopPropagation();
        clearGlobalError();

        runSubmitValidationAndMaybeSubmit().catch(() => {
            showGlobalError("회원가입 요청 처리 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
            state.submitting = false;
            if (signupBtn) signupBtn.disabled = false;
        });
    }, true);
})();
