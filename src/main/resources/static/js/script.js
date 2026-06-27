// Set Mood Selection in Hidden Input
function setMood(element) {
    document.querySelectorAll('.mood-btn').forEach(btn => btn.classList.remove('active'));
    element.classList.add('active');
    document.getElementById('selectedMoodInput').value = element.getAttribute('data-mood');
}

// Scroll chat to bottom on load
window.addEventListener('DOMContentLoaded', () => {
    const chatBox = document.getElementById('chatContainer');
    if (chatBox) {
        chatBox.scrollTop = chatBox.scrollHeight;
    }
    if (typeof journalsData !== 'undefined') {
        renderStressGraph(journalsData);
    }
});

// Chat AJAX messaging functionality
async function sendChatMessage() {
    const input = document.getElementById('chatMsgInput');
    const message = input.value.trim();
    if (!message) return;

    // Clear Input field
    input.value = '';

    // Add user message to UI
    const chatBox = document.getElementById('chatContainer');
    const userMsgEl = document.createElement('div');
    userMsgEl.className = 'chat-msg user';
    userMsgEl.textContent = message;
    chatBox.appendChild(userMsgEl);
    chatBox.scrollTop = chatBox.scrollHeight;

    // Add typing indicator
    const typingEl = document.createElement('div');
    typingEl.className = 'chat-msg ai typing';
    typingEl.innerHTML = '<i class="fa-solid fa-ellipsis fa-bounce"></i> Thinking...';
    chatBox.appendChild(typingEl);
    chatBox.scrollTop = chatBox.scrollHeight;

    try {
        // Fetch dynamic security elements if present
        const response = await fetch('/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                // Inject CSRF header if required by Spring Security configuration
            },
            body: JSON.stringify({ message: message })
        });

        if (!response.ok) {
            throw new Error("Chat companion error response");
        }

        const result = await response.json();

        // Remove typing indicator
        typingEl.remove();

        // Add AI response to UI
        const aiMsgEl = document.createElement('div');
        aiMsgEl.className = 'chat-msg ai';
        aiMsgEl.textContent = result.ai;
        chatBox.appendChild(aiMsgEl);
        chatBox.scrollTop = chatBox.scrollHeight;

    } catch (err) {
        console.error("Chat failure: ", err);
        typingEl.remove();

        const errEl = document.createElement('div');
        errEl.className = 'chat-msg ai';
        errEl.style.borderColor = 'var(--danger)';
        errEl.textContent = "Apologies, I encountered a connection drop. I am still here to support you.";
        chatBox.appendChild(errEl);
        chatBox.scrollTop = chatBox.scrollHeight;
    }
}

// Render dynamic SVG stress graph
function renderStressGraph(journals) {
    const svg = document.getElementById('stressChartSvg');
    if (!svg || !journals || journals.length === 0) return;

    // Reverse list to show chronological left-to-right progression
    const chartData = [...journals].reverse();

    const width = svg.clientWidth || 500;
    const height = 180;
    const padding = 25;

    const chartWidth = width - (padding * 2);
    const chartHeight = height - (padding * 2);

    svg.innerHTML = ''; // Clear SVG

    // Helper scales
    const getX = (index) => {
        if (chartData.length <= 1) return padding + (chartWidth / 2);
        return padding + (index * (chartWidth / (chartData.length - 1)));
    };

    const getY = (val) => {
        // Stress level mapped 0% - 100%
        return height - padding - (val * (chartHeight / 100));
    };

    // 1. Draw grid background lines
    const gridLines = [0, 25, 50, 75, 100];
    gridLines.forEach(lvl => {
        const lineY = getY(lvl);
        const line = document.createElementNS('http://www.w3.org/2000/svg', 'line');
        line.setAttribute('x1', padding);
        line.setAttribute('y1', lineY);
        line.setAttribute('x2', width - padding);
        line.setAttribute('y2', lineY);
        line.setAttribute('stroke', 'rgba(0, 0, 0, 0.1)');
        line.setAttribute('stroke-dasharray', '4,4');
        svg.appendChild(line);

        const text = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        text.setAttribute('x', padding - 5);
        text.setAttribute('y', lineY + 3);
        text.setAttribute('text-anchor', 'end');
        text.setAttribute('fill', 'var(--text-muted)');
        text.setAttribute('font-size', '10px');
        text.textContent = lvl + '%';
        svg.appendChild(text);
    });

    // 2. Build line points path
    let pathString = '';
    let areaString = `M ${getX(0)} ${getY(0)}`;

    chartData.forEach((d, idx) => {
        const x = getX(idx);
        const y = getY(d.stressLevel);
        if (idx === 0) {
            pathString += `M ${x} ${y}`;
            areaString = `M ${x} ${height - padding} L ${x} ${y}`;
        } else {
            pathString += ` L ${x} ${y}`;
            areaString += ` L ${x} ${y}`;
        }
    });
    areaString += ` L ${getX(chartData.length - 1)} ${height - padding} Z`;

    // 3. Draw linear gradient for area fill
    const defs = document.createElementNS('http://www.w3.org/2000/svg', 'defs');
    const gradient = document.createElementNS('http://www.w3.org/2000/svg', 'linearGradient');
    gradient.setAttribute('id', 'chartGrad');
    gradient.setAttribute('x1', '0');
    gradient.setAttribute('y1', '0');
    gradient.setAttribute('x2', '0');
    gradient.setAttribute('y2', '1');

    const stop1 = document.createElementNS('http://www.w3.org/2000/svg', 'stop');
    stop1.setAttribute('offset', '0%');
    stop1.setAttribute('stop-color', 'rgba(99, 102, 241, 0.25)');

    const stop2 = document.createElementNS('http://www.w3.org/2000/svg', 'stop');
    stop2.setAttribute('offset', '100%');
    stop2.setAttribute('stop-color', 'rgba(99, 102, 241, 0.0)');

    gradient.appendChild(stop1);
    gradient.appendChild(stop2);
    defs.appendChild(gradient);
    svg.appendChild(defs);

    // 4. Draw area path
    if (chartData.length > 0) {
        const areaPath = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        areaPath.setAttribute('d', areaString);
        areaPath.setAttribute('fill', 'url(#chartGrad)');
        svg.appendChild(areaPath);
    }

    // 5. Draw line path
    if (chartData.length > 0) {
        const linePath = document.createElementNS('http://www.w3.org/2000/svg', 'path');
        linePath.setAttribute('d', pathString);
        linePath.setAttribute('fill', 'none');
        linePath.setAttribute('stroke', 'var(--primary)');
        linePath.setAttribute('stroke-width', '3.5');
        linePath.setAttribute('stroke-linecap', 'round');
        linePath.setAttribute('stroke-linejoin', 'round');
        svg.appendChild(linePath);
    }

    // 6. Draw dots and values
    chartData.forEach((d, idx) => {
        const x = getX(idx);
        const y = getY(d.stressLevel);

        // Outer glow dot
        const glowCircle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        glowCircle.setAttribute('cx', x);
        glowCircle.setAttribute('cy', y);
        glowCircle.setAttribute('r', '7.5');
        glowCircle.setAttribute('fill', 'rgba(99, 102, 241, 0.3)');
        svg.appendChild(glowCircle);

        // Inner core dot
        const coreCircle = document.createElementNS('http://www.w3.org/2000/svg', 'circle');
        coreCircle.setAttribute('cx', x);
        coreCircle.setAttribute('cy', y);
        coreCircle.setAttribute('r', '4');
        coreCircle.setAttribute('fill', '#fff');
        svg.appendChild(coreCircle);

        // Value text label
        const valText = document.createElementNS('http://www.w3.org/2000/svg', 'text');
        valText.setAttribute('x', x);
        valText.setAttribute('y', y - 12);
        valText.setAttribute('text-anchor', 'middle');
        valText.setAttribute('fill', 'var(--text-main)');
        valText.setAttribute('font-size', '11px');
        valText.setAttribute('font-weight', '700');
        valText.textContent = d.stressLevel + '%';
        svg.appendChild(valText);
    });
}
