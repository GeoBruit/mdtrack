document.addEventListener('DOMContentLoaded', () => {
    const modal = document.getElementById('profileModal');
    const closeBtn = document.getElementById('closeProfileModal');
    const hasProfile = document.body.dataset.hasProfile === 'true';

    if (!hasProfile && modal) {
        modal.classList.remove('hidden');
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', () => {
            modal.classList.add('hidden');
        });
    }
});


// tab toggle logic for profile view
document.addEventListener("DOMContentLoaded", function () {
    const notesTab = document.getElementById("notesTab");
    const docsTab = document.getElementById("docsTab");
    const notesSection = document.getElementById("notesSection");
    const docsSection = document.getElementById("docsSection");

    if (notesTab && docsTab && notesSection && docsSection) {
        notesTab.addEventListener("click", () => {
            notesTab.classList.add("text-blue-700", "border-blue-700");
            notesTab.classList.remove("text-gray-600");
            docsTab.classList.remove("text-blue-700", "border-blue-700");
            docsTab.classList.add("text-gray-600");

            notesSection.classList.remove("hidden");
            docsSection.classList.add("hidden");
        });

        docsTab.addEventListener("click", () => {
            docsTab.classList.add("text-blue-700", "border-blue-700");
            docsTab.classList.remove("text-gray-600");
            notesTab.classList.remove("text-blue-700", "border-blue-700");
            notesTab.classList.add("text-gray-600");

            docsSection.classList.remove("hidden");
            notesSection.classList.add("hidden");
        });
    }
});