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