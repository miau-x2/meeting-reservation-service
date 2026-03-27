(() => {
    const paginationEl = document.querySelector("[data-pagination]");

    function toNumber(value, fallback) {
        const parsed = Number(value);
        return Number.isFinite(parsed) ? parsed : fallback;
    }

    function buildPageUrl(baseUrl, page, pageSize, floor) {
        const params = new URLSearchParams();
        params.set("page", String(page));
        params.set("size", String(pageSize));
        if (floor) {
            params.set("floor", floor);
        }
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

    renderPagination();
    bindFilters();
})();
